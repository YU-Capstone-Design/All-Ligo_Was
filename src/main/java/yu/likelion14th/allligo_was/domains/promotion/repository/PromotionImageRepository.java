package yu.likelion14th.allligo_was.domains.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionImage;

import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import java.util.List;
import java.util.Optional;

public interface PromotionImageRepository extends JpaRepository<PromotionImage, Long> {
    List<PromotionImage> findAllByPromotion(Promotion promotion);
    List<PromotionImage> findAllByPromotionPromotionId(Long promotionId);
    void deleteAllByPromotionPromotionId(Long promotionId);
    Optional<PromotionImage> findFirstByPromotionPromotionIdOrderByImageIdAsc(Long promotionId);
}
