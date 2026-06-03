package yu.likelion14th.allligo_was.domains.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.notification.api.NotificationAPI;
import yu.likelion14th.allligo_was.domains.notification.dto.response.NotificationResDto;
import yu.likelion14th.allligo_was.domains.notification.service.NotificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationAPI {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public ResponseEntity<List<NotificationResDto>> getMyNotifications() {
        Long userId = getCurrentUserId();

        List<NotificationResDto> response =
                notificationService.getMyNotifications(userId);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId
    ) {
        Long userId = getCurrentUserId();

        notificationService.readNotification(userId, notificationId);

        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}