package yu.likelion14th.allligo_was.domains.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.notification.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop20ByUserUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByNotificationIdAndUserUserId(Long notificationId, Long userId);

    boolean existsByContentContentId(Long contentId);
}