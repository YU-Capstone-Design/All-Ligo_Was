package yu.likelion14th.allligo_was.domains.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
