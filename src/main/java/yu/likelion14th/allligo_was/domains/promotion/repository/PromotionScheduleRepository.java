package yu.likelion14th.allligo_was.domains.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface PromotionScheduleRepository extends JpaRepository<PromotionSchedule,Long> {
    // List<PromotionSchedule> findValidSchedules(String currentDayOfWeek, LocalTime oneHourLater, LocalDate today);
    List<PromotionSchedule> findAllByPublishTimeBetween(LocalDateTime start, LocalDateTime end);
}
