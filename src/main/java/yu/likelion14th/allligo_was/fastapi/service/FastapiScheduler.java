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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastapiScheduler {

    private final PromotionScheduleRepository scheduleRepository;
    private final FastapiClientService fastapiClientService;

    // 매분 0초에 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional(readOnly = true)
    public void executeTwoTrackScheduler() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime oneHourLater = now.plusHours(1);

        log.info("Two-Track Scheduler Running... now: {}, oneHourLater: {}", now, oneHourLater);

        // Track A (T - 1시간): 영상 생성 요청
        List<PromotionSchedule> generateSchedules = scheduleRepository.findAllByPublishTimeBetween(
                oneHourLater, oneHourLater.plusSeconds(59));

        for (PromotionSchedule schedule : generateSchedules) {
            log.info("Track A: Requesting content generation for schedule ID: {}", schedule.getScheduleId());
            Promotion promotion = schedule.getPromotion();
            
            // TODO: Promotion과 PromotionTag에서 실제 분위기태그, 해시태그 추출 (현재는 임시값 또는 기본값 처리)
            FastapiGenerateReqDto reqDto = FastapiGenerateReqDto.builder()
                    .scheduleId(schedule.getScheduleId())
                    .moodTag("") // TODO: 태그 매핑
                    .hashTag("") // TODO: 태그 매핑
                    .prompt(promotion != null ? promotion.getPrompt() : "")
                    .uploadDay(schedule.getDayOfWeek())
                    .uploadTime(schedule.getPublishTime() != null ? schedule.getPublishTime().toLocalTime().toString() : "morning")
                    .build();

            try {
                fastapiClientService.generateContent(reqDto, null);
            } catch (Exception e) {
                log.error("Track A generation request failed for schedule ID: {}", schedule.getScheduleId(), e);
            }
        }

        // Track B (T - 0시간): 유튜브 업로드 요청
        List<PromotionSchedule> uploadSchedules = scheduleRepository.findAllByPublishTimeBetween(
                now, now.plusSeconds(59));

        for (PromotionSchedule schedule : uploadSchedules) {
            log.info("Track B: Requesting youtube upload for schedule ID: {}", schedule.getScheduleId());
            Promotion promotion = schedule.getPromotion();
            
            // TODO: Webhook 등으로 저장해둔 영상 경로(localVideoPath)를 DB에서 가져와야 함
            yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadReqDto uploadReq = yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadReqDto.builder()
                    .scheduleId(schedule.getScheduleId())
                    .localVideoPath("static/videos/shortform_임시.mp4") // 실제 경로로 변경 필요
                    .title(promotion != null ? promotion.getPrompt() : "쇼츠 제목")
                    .description("쇼츠 설명")
                    .tags(java.util.List.of("쇼츠", "마케팅"))
                    .build();

            try {
                fastapiClientService.uploadToYoutube(uploadReq);
            } catch (Exception e) {
                log.error("Track B upload request failed for schedule ID: {}", schedule.getScheduleId(), e);
            }
        }
    }
}
