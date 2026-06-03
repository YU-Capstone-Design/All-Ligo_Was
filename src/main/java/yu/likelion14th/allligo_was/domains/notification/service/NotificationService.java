package yu.likelion14th.allligo_was.domains.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.notification.dto.response.NotificationResDto;
import yu.likelion14th.allligo_was.domains.notification.entity.Notification;
import yu.likelion14th.allligo_was.domains.notification.repository.NotificationRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.user.entity.User;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 콘텐츠 생성 완료 알림을 생성합니다.
     *
     * FastAPI webhook 성공으로 Content가 GENERATED 상태로 저장된 직후 호출합니다.
     * 같은 contentId에 대한 알림이 이미 존재하면 중복 생성하지 않습니다.
     *
     * @param content 생성 완료된 콘텐츠
     */
    public void createContentGeneratedNotification(Content content) {
        if (content == null || content.getContentId() == null) {
            return;
        }

        if (notificationRepository.existsByContentContentId(content.getContentId())) {
            return;
        }

        PromotionExecution execution = content.getPromotionExecution();
        if (execution == null) {
            return;
        }

        Promotion promotion = execution.getPromotion();
        if (promotion == null || promotion.getUser() == null) {
            return;
        }

        User user = promotion.getUser();

        String promotionTitle = promotion.getPromotionTitle();

        if (promotionTitle == null || promotionTitle.isBlank()) {
            promotionTitle = "홍보";
        }

        Notification notification = Notification.builder()
                .notificationTitle(promotionTitle + " 콘텐츠 생성 완료")
                .message("생성된 콘텐츠를 확인해보세요!")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .content(content)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 로그인한 사용자의 최신 알림 20개를 조회합니다.
     *
     * @param userId 현재 로그인한 사용자 ID
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationResDto> getMyNotifications(Long userId) {
        return notificationRepository.findTop20ByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResDto::fromEntity)
                .toList();
    }

    /**
     * 알림을 읽음 처리합니다.
     *
     * 알림 목록 조회만으로는 읽음 처리하지 않고,
     * 사용자가 알림을 클릭했을 때 호출합니다.
     *
     * @param userId 현재 로그인한 사용자 ID
     * @param notificationId 읽음 처리할 알림 ID
     */
    public void readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByNotificationIdAndUserUserId(notificationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
    }
}