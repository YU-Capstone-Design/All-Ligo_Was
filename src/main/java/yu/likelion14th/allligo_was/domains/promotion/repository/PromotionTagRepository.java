package yu.likelion14th.allligo_was.domains.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionTag;
import java.util.List;

public interface PromotionTagRepository extends JpaRepository<PromotionTag, Long> {
    List<PromotionTag> findAllByPromotion(Promotion promotion);
    List<PromotionTag> findAllByPromotionPromotionId(Long promotionId);
    void deleteAllByPromotionPromotionId(Long promotionId);
}
