package yu.likelion14th.allligo_was.domains.promotion.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("DELETE FROM PromotionExecution pe WHERE pe.promotion.promotionId = :promotionId AND pe.status = 'PENDING'")
    void deletePendingExecutionsByPromotionId(@Param("promotionId") Long promotionId);

    @Modifying
    @Query("UPDATE PromotionExecution pe SET pe.promotionSchedule = null WHERE pe.promotion.promotionId = :promotionId")
    void nullifyScheduleIdByPromotionId(@Param("promotionId") Long promotionId);

    // 생성 요청 응답의 taskId 만 저장한다. 그 사이 웹훅이 바꾼 상태(SUCCESS/FAILED)는 건드리지 않는다.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PromotionExecution pe SET pe.taskId = :taskId WHERE pe.executionId = :executionId")
    int updateTaskId(@Param("executionId") Long executionId, @Param("taskId") String taskId);

    // 생성 요청 실패 시, 아직 PROCESSING 인 경우에만 FAILED 로 바꾼다.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PromotionExecution pe SET pe.status = 'FAILED', pe.errorMessage = :errorMessage "
            + "WHERE pe.executionId = :executionId AND pe.status = 'PROCESSING'")
    int markFailedIfProcessing(@Param("executionId") Long executionId, @Param("errorMessage") String errorMessage);

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
