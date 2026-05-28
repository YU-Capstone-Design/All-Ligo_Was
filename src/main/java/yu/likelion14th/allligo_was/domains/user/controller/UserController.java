package yu.likelion14th.allligo_was.domains.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.user.api.UserAPI;
import yu.likelion14th.allligo_was.domains.user.dto.request.UserProfileUpdateReqDto;
import yu.likelion14th.allligo_was.domains.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserAPI {

    private final UserService userService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<?> getMyPage() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(userService.getMyPage(userId));
    }

    @Override
    @PatchMapping("/me/profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UserProfileUpdateReqDto dto
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(userService.updateProfile(userId, dto));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}