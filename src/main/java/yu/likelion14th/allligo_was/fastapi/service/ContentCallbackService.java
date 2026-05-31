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

        PromotionExecution execution = null;
        if (dto.getTaskId() != null && !dto.getTaskId().isEmpty()) {
            execution = executionRepository.findByTaskId(dto.getTaskId()).orElse(null);
        }

        // taskId로 못 찾았고 scheduleId가 있다면 기존 로직(최근 실행 건 조회)을 Fallback으로 사용
        if (execution == null && dto.getScheduleId() != null) {
            try {
                Long scheduleId = Long.parseLong(dto.getScheduleId());
                PromotionSchedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
                if (schedule != null) {
                    execution = executionRepository.findFirstByPromotionScheduleOrderByExecutedAtDesc(schedule)
                            .orElse(null);
                }
            } catch (NumberFormatException ignored) {}
        }

        if (execution == null) {
            log.warn("No PromotionExecution found for Task ID: {} and Schedule ID: {}", dto.getTaskId(), dto.getScheduleId());
            return;
        }

        // 상태 업데이트
        execution.setStatus(dto.getStatus());
        if ("FAILED".equalsIgnoreCase(dto.getStatus()) && dto.getError() != null) {
            execution.setErrorMessage(dto.getError());
        }
        executionRepository.save(execution);

        if ("SUCCESS".equalsIgnoreCase(dto.getStatus()) && dto.getData() != null) {
            FastapiWebhookDto.WebhookData data = dto.getData();
            
            // 기존 Content가 있는지 확인 후 없으면 생성
            PromotionExecution finalExecution = execution;
            Content content = contentRepository.findByPromotionExecution(execution).orElseGet(() ->
                Content.builder()
                    .promotionExecution(finalExecution)
                    .createdAt(LocalDateTime.now())
                    .build()
            );

            // 1. contentType에 따른 텍스트 분기 처리
            if ("POST".equalsIgnoreCase(data.getContentType())) {
                content.setBodyText(data.getGeneratedText()); // 긴 글은 bodyText에 저장
            } else if ("VIDEO".equalsIgnoreCase(data.getContentType())) {
                content.setCaption(data.getGeneratedText());  // 짧은 영상 자막은 caption에 저장
            }

            // 2. 미디어 URL 처리
            if (data.getPosterUrl() != null) {
                content.setPosterUrl(data.getPosterUrl()); // 명칭 변경된 필드 적용
            }
            if (data.getS3VideoUrl() != null) {
                content.setS3VideoUrl(data.getS3VideoUrl());
                content.setLocalVideoPath(data.getLocalVideoPath());
            }

            content.setStatus("GENERATED");
            contentRepository.save(content);
            log.info("Successfully saved Content for Execution ID: {}", execution.getExecutionId());
        } else {
            log.warn("Webhook failed or data missing. TaskId: {}, Error: {}", dto.getTaskId(), dto.getError());
        }
    }
}
