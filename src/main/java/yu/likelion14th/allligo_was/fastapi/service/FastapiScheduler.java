package yu.likelion14th.allligo_was.fastapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionScheduleRepository;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadReqDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastapiScheduler {

    private final PromotionScheduleRepository scheduleRepository;
    private final FastapiClientService fastapiClientService;
    private final yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository executionRepository;
    private final yu.likelion14th.allligo_was.domains.content.repository.ContentRepository contentRepository;

    // 매분 0초에 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void executeTwoTrackScheduler() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        
        // 기존 로직: 1시간 전 생성 요청 (운영용)
        // LocalDateTime oneHourLater = now.plusHours(1);
        
        // 테스트 로직: 2분 전 생성 요청 (테스트용, 테스트 완료 후 위 주석 해제 및 본 줄 삭제)
        LocalDateTime oneHourLater = now.plusMinutes(2);

        log.info("Two-Track Scheduler Running... now: {}, oneHourLater: {}", now, oneHourLater);

        // Track A (T - 1시간): 영상 생성 요청
        List<PromotionSchedule> generateSchedules = scheduleRepository.findAllByPublishTimeBetween(
                oneHourLater, oneHourLater.plusSeconds(59));

        for (PromotionSchedule schedule : generateSchedules) {
            log.info("Track A: Requesting content generation for schedule ID: {}", schedule.getScheduleId());
            Promotion promotion = schedule.getPromotion();

            // PromotionExecution 생성 및 저장
            yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution execution = yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution.builder()
                    .executedAt(LocalDateTime.now())
                    .status("PENDING")
                    .promotion(promotion)
                    .promotionSchedule(schedule)
                    .build();
            executionRepository.save(execution);

            // TODO: Promotion과 PromotionTag에서 실제 분위기태그, 해시태그 추출 (현재는 임시값 또는 기본값 처리)
            FastapiGenerateReqDto reqDto = FastapiGenerateReqDto.builder()
                    .scheduleId(schedule.getScheduleId())
                    .moodTag("밝은, 쾌활한") // TODO: 태그 매핑 (임시로 기본값 입력)
                    .hashTag("#마케팅 #이벤트") // TODO: 태그 매핑 (임시로 기본값 입력)
                    .prompt(promotion != null ? promotion.getPrompt() : "")
                    .uploadDay(schedule.getDayOfWeek())
                    .uploadTime(schedule.getPublishTime() != null ? schedule.getPublishTime().toLocalTime().toString() : "morning")
                    .build();

            try {
                yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto response = fastapiClientService.generateContent(reqDto, null);
                if (response != null && response.getTaskId() != null) {
                    execution.setTaskId(response.getTaskId());
                    // status is likely "PROCESSING" from response, or we keep "PENDING"
                    if (response.getStatus() != null) {
                        execution.setStatus(response.getStatus());
                    }
                    executionRepository.save(execution);
                    log.info("Track A generation requested successfully. Task ID: {}", response.getTaskId());
                }
            } catch (Exception e) {
                log.error("Track A generation request failed for schedule ID: {}", schedule.getScheduleId(), e);
                execution.setStatus("FAILED");
                execution.setErrorMessage(e.getMessage());
                executionRepository.save(execution);
            }
        }

        // Track B (T - 0시간): 유튜브 업로드 요청
        List<PromotionSchedule> uploadSchedules = scheduleRepository.findAllByPublishTimeBetween(
                now, now.plusSeconds(59));

        for (PromotionSchedule schedule : uploadSchedules) {
            log.info("Track B: Requesting youtube upload for schedule ID: {}", schedule.getScheduleId());
            Promotion promotion = schedule.getPromotion();

            // 스케줄에 연결된 최근 Execution 조회
            yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution execution = executionRepository
                    .findFirstByPromotionScheduleOrderByExecutedAtDesc(schedule).orElse(null);

            if (execution == null) {
                log.warn("Track B: No PromotionExecution found for schedule ID: {}", schedule.getScheduleId());
                continue;
            }

            // Execution에 연결된 Content 조회
            yu.likelion14th.allligo_was.domains.content.entity.Content content = contentRepository
                    .findByPromotionExecution(execution).orElse(null);

            if (content == null || content.getLocalVideoPath() == null) {
                log.warn("Track B: No Content or LocalVideoPath found for execution ID: {}", execution.getExecutionId());
                continue;
            }

            FastapiUploadReqDto uploadReq = FastapiUploadReqDto.builder()
                    .scheduleId(schedule.getScheduleId())
                    .localVideoPath(content.getLocalVideoPath())
                    .title(promotion != null ? promotion.getPrompt() : "쇼츠 제목")
                    .description("쇼츠 설명")
                    .tags(java.util.List.of("쇼츠", "마케팅"))
                    .build();

            try {
                yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto response = fastapiClientService.uploadToYoutube(uploadReq);
                if (response != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
                    content.setUploadVideoUrl(response.getYoutubeUrl());
                    content.setUploadedAt(LocalDateTime.now());
                    contentRepository.save(content);
                    log.info("Track B: Upload success. YouTube URL saved: {}", response.getYoutubeUrl());
                }
            } catch (Exception e) {
                log.error("Track B upload request failed for schedule ID: {}", schedule.getScheduleId(), e);
            }
        }
    }
}
