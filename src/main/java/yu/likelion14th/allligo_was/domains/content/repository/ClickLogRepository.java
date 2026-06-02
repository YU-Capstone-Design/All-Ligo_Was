package yu.likelion14th.allligo_was.domains.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.content.entity.ClickLog;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
}
