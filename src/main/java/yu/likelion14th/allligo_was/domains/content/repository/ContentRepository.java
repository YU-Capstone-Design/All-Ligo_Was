package yu.likelion14th.allligo_was.domains.content.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByPromotionExecution(PromotionExecution execution);
    void deleteAllByPromotionExecutionPromotionPromotionId(Long promotionId);

    Optional<Content> findByContentIdAndPromotionExecutionPromotionUserUserId(
            Long contentId,
            Long userId
    );

    @Query("SELECT c, COUNT(cl) " +
            "FROM Content c " +
            "JOIN c.promotionExecution pe " +
            "JOIN pe.promotion p " +
            "LEFT JOIN ClickLog cl ON cl.content = c " +
            "WHERE p.user.userId = :userId AND c.status = 'PUBLISHED' " +
            "GROUP BY c " +
            "ORDER BY COUNT(cl) DESC")
    List<Object[]> findTopContentsWithClickCountByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
