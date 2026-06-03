package yu.likelion14th.allligo_was.domains.promotion.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;

public interface PromotionExecutionRepository extends JpaRepository<PromotionExecution, Long> {
    Optional<PromotionExecution> findFirstByPromotionScheduleOrderByExecutedAtDesc(PromotionSchedule schedule);
    Optional<PromotionExecution> findByTaskId(String taskId);
    boolean existsByPromotionScheduleAndExecutedAt(PromotionSchedule schedule, LocalDateTime executedAt);

    List<PromotionExecution> findAllByStatusAndExecutedAtBetween(String status, LocalDateTime start, LocalDateTime end);
    List<PromotionExecution> findAllByExecutedAtBetween(LocalDateTime start, LocalDateTime end);

    void deleteAllByPromotionPromotionId(Long promotionId);

    @Query("""
        SELECT DISTINCT pe
        FROM PromotionExecution pe
        JOIN FETCH pe.promotion p
        LEFT JOIN FETCH pe.content c
        WHERE p.user.userId = :userId
          AND pe.executedAt BETWEEN :now AND :endTime
        ORDER BY pe.executedAt ASC
        """)
    List<PromotionExecution> findQueueByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("endTime") LocalDateTime endTime
    );
}
