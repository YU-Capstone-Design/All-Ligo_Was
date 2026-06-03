package yu.likelion14th.allligo_was.domains.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.notification.entity.Notification;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResDto {

    @Schema(description = "알림 ID", example = "1")
    private Long notificationId;

    @Schema(description = "연결된 콘텐츠 ID", example = "10")
    private Long contentId;

    @Schema(description = "알림 제목", example = "여름 신메뉴 홍보 콘텐츠 생성 완료")
    private String notificationTitle;

    @Schema(description = "알림 메시지", example = "생성된 콘텐츠를 확인해보세요!")
    private String message;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "알림 생성 시간", example = "2026-06-03T15:30:00")
    private LocalDateTime createdAt;

    public static NotificationResDto fromEntity(Notification notification) {
        return NotificationResDto.builder()
                .notificationId(notification.getNotificationId())
                .contentId(notification.getContent().getContentId())
                .notificationTitle(notification.getNotificationTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}