package yu.likelion14th.allligo_was.domains.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findAllByUserUserIdOrderByCreatedAtDesc(Long userId);
    List<Promotion> findAllByDeadlineAfter(java.time.LocalDateTime now);

    Optional<Promotion> findByPromotionIdAndUserUserId(Long promotionId, Long userId);

    boolean existsByPromotionIdAndUserUserId(Long promotionId, Long userId);
}
