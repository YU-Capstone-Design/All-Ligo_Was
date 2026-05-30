package yu.likelion14th.allligo_was.fastapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionScheduleRepository;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiWebhookDto;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentCallbackService {

    private final PromotionScheduleRepository scheduleRepository;
    private final PromotionExecutionRepository executionRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void processWebhook(FastapiWebhookDto dto) {
        log.info("Received webhook callback from FastAPI. TaskId: {}, Status: {}", dto.getTaskId(), dto.getStatus());

        if (dto.getScheduleId() == null) {
            log.warn("Webhook received without scheduleId. TaskId: {}", dto.getTaskId());
            return;
        }

        Long scheduleId;
        try {
            scheduleId = Long.parseLong(dto.getScheduleId());
        } catch (NumberFormatException e) {
            log.error("Invalid scheduleId format: {}", dto.getScheduleId());
            return;
        }

        PromotionSchedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            log.warn("PromotionSchedule not found for ID: {}", scheduleId);
            return;
        }

        PromotionExecution execution = executionRepository.findFirstByPromotionScheduleOrderByExecutedAtDesc(schedule)
                .orElse(null);

        if (execution == null) {
            log.warn("No PromotionExecution found for Schedule ID: {}", scheduleId);
            return;
        }

        // 상태 업데이트
        execution.setStatus(dto.getStatus());
        executionRepository.save(execution);

        if ("SUCCESS".equalsIgnoreCase(dto.getStatus()) && dto.getData() != null) {
            FastapiWebhookDto.WebhookData data = dto.getData();
            
            // 기존 Content가 있는지 확인 후 없으면 생성
            Content content = contentRepository.findByPromotionExecution(execution).orElseGet(() -> 
                Content.builder()
                    .promotionExecution(execution)
                    .createdAt(LocalDateTime.now())
                    .build()
            );

            content.setBodyText(data.getGeneratedText());
            content.setS3VideoUrl(data.getS3VideoUrl());
            content.setLocalVideoPath(data.getLocalVideoPath());
            
            // 이미지 결과도 필요하다면 (블로그 용)
            if (data.getGeneratedImageUrl() != null && content.getImageUrl() == null) {
                content.setImageUrl(data.getGeneratedImageUrl());
            }

            content.setStatus("GENERATED");
            contentRepository.save(content);
            log.info("Successfully saved Content for Execution ID: {}", execution.getExecutionId());
        } else {
            log.warn("Webhook failed or data missing. TaskId: {}, Error: {}", dto.getTaskId(), dto.getError());
        }
    }
}
