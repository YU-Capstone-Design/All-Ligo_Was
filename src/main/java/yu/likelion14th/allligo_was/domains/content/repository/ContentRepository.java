package yu.likelion14th.allligo_was.domains.content.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByPromotionExecution(PromotionExecution execution);
}
