package yu.likelion14th.allligo_was.domains.promotion.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;

public interface PromotionExecutionRepository extends JpaRepository<PromotionExecution, Long> {
    Optional<PromotionExecution> findFirstByPromotionScheduleOrderByExecutedAtDesc(PromotionSchedule schedule);
}
