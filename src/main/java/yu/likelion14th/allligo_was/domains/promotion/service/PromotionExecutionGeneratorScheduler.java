package yu.likelion14th.allligo_was.domains.promotion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionExecutionGeneratorScheduler {

    private final PromotionRepository promotionRepository;
    private final PromotionScheduleRepository promotionScheduleRepository;
    private final PromotionExecutionRepository promotionExecutionRepository;

    // 매시간 0분에 실행
    @Scheduled(cron = "0 1/2 * * * *")
    @Transactional
    public void generateExecutionsForNext7Days() {
        LocalDateTime now = LocalDateTime.now();
        log.info("PromotionExecutionGeneratorScheduler Running... now: {}", now);

        List<Promotion> activePromotions = promotionRepository.findAllByDeadlineAfter(now);
        List<PromotionExecution> newExecutions = new ArrayList<>();

        for (Promotion promotion : activePromotions) {
            List<PromotionSchedule> schedules = promotionScheduleRepository.findAllByPromotionPromotionId(promotion.getPromotionId());

            for (PromotionSchedule schedule : schedules) {
                if (schedule.getDayOfWeek() == null || schedule.getPublishTime() == null) {
                    continue;
                }

                try {
                    DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(schedule.getDayOfWeek().toUpperCase());
                    LocalTime time = schedule.getPublishTime().toLocalTime();

                    LocalDate targetDate = now.toLocalDate().with(TemporalAdjusters.nextOrSame(targetDayOfWeek));
                    LocalDateTime targetDateTime = LocalDateTime.of(targetDate, time);

                    // 만약 오늘 날짜인데 이미 시간이 지난 경우, 7일 뒤(다음주)로 계산
                    if (targetDateTime.isBefore(now)) {
                        targetDateTime = targetDateTime.plusDays(7);
                    }

                    // 데드라인을 넘기지 않는지 검사
                    if (promotion.getDeadline() != null && targetDateTime.isAfter(promotion.getDeadline())) {
                        continue;
                    }

                    // 중복 생성 방지 검사
                    if (!promotionExecutionRepository.existsByPromotionScheduleAndExecutedAt(schedule, targetDateTime)) {
                        PromotionExecution execution = PromotionExecution.builder()
                                .promotion(promotion)
                                .promotionSchedule(schedule)
                                .executedAt(targetDateTime)
                                .status("PENDING")
                                .build();
                        
                        newExecutions.add(execution);
                        log.info("Generated new PromotionExecution (PENDING) for Schedule ID: {} at {}", schedule.getScheduleId(), targetDateTime);
                    }

                } catch (IllegalArgumentException e) {
                    log.warn("Invalid DayOfWeek in Schedule ID: {}", schedule.getScheduleId());
                }
            }
        }

        if (!newExecutions.isEmpty()) {
            promotionExecutionRepository.saveAll(newExecutions);
            log.info("Successfully saved {} new PromotionExecutions.", newExecutions.size());
        } else {
            log.info("No new PromotionExecutions to generate.");
        }
    }
}
