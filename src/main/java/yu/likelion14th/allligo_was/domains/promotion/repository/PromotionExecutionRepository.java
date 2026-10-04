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

    // 유튜브 업로드 대상: 구간 안에서 생성 완료(GENERATED)된 영상 콘텐츠가 있는 실행
    @Query("SELECT pe.executionId FROM PromotionExecution pe JOIN pe.content c "
            + "WHERE pe.executedAt BETWEEN :from AND :to "
            + "AND c.status = 'GENERATED' AND c.localVideoPath IS NOT NULL "
            + "ORDER BY pe.executedAt ASC")
    List<Long> findUploadTargetIds(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // 지연 허용 시간이 지나도록 생성 요청·결과 수신이 끝나지 않은 실행을 FAILED 로 정리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PromotionExecution pe SET pe.status = 'FAILED', pe.errorMessage = :errorMessage "
            + "WHERE pe.status = :status AND pe.executedAt >= :from AND pe.executedAt < :to")
    int expireByStatus(@Param("status") String status,
                       @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to,
                       @Param("errorMessage") String errorMessage);

    // 지연 허용 시간 안에 유튜브 업로드를 못 한 영상의 실행을 FAILED 로 정리 (콘텐츠는 GENERATED 로 남아 미리보기 가능)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PromotionExecution pe SET pe.status = 'FAILED', pe.errorMessage = :errorMessage "
            + "WHERE pe.status = 'SUCCESS' AND pe.executedAt >= :from AND pe.executedAt < :to "
            + "AND EXISTS (SELECT c.contentId FROM Content c WHERE c.promotionExecution = pe "
            + "AND c.status = 'GENERATED' AND c.localVideoPath IS NOT NULL)")
    int expireNotUploaded(@Param("from") LocalDateTime from,
                          @Param("to") LocalDateTime to,
                          @Param("errorMessage") String errorMessage);

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
