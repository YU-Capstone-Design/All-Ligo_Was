package yu.likelion14th.allligo_was.domains.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.user.dto.request.UserProfileUpdateReqDto;
import yu.likelion14th.allligo_was.domains.user.service.UserService;
import yu.likelion14th.allligo_was.security.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<?> getMyPage(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Long userId = extractUserId(authorizationHeader);
        return ResponseEntity.ok(userService.getMyPage(userId));
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserProfileUpdateReqDto dto
    ) {
        Long userId = extractUserId(authorizationHeader);
        return ResponseEntity.ok(userService.updateProfile(userId, dto));
    }

    private Long extractUserId(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtUtil.parseUserId(token);
    }
}