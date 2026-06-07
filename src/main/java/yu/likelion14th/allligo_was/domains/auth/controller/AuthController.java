package yu.likelion14th.allligo_was.domains.auth.controller;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import yu.likelion14th.allligo_was.domains.auth.api.AuthAPI;
import yu.likelion14th.allligo_was.domains.auth.dto.request.EmailAddressReqDto;
import yu.likelion14th.allligo_was.domains.auth.service.AuthService;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.domains.auth.dto.request.SignUpReqDto;
import yu.likelion14th.allligo_was.domains.auth.dto.request.LoginReqDto;
import yu.likelion14th.allligo_was.exception.CustomException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

    private final AuthService authService;

    @GetMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(authService.checkEmailDuplication(email));
    }

    @PostMapping("/email/send")
    public ResponseEntity<?> sendVerificationEmail(@Valid @RequestBody EmailAddressReqDto dto) {
        return ResponseEntity.ok(authService.sendVerificationEmail(dto));
    }

    @Value("${app.frontend.verify-success-url}")
    private String frontendVerifySuccessUrl;

    @Value("${app.frontend.verify-fail-url}")
    private String frontendVerifyFailUrl;

    @GetMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam("email") String email,
            @RequestParam("token") String token) {
        try {
            authService.verifyEmail(email, token);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendVerifySuccessUrl))
                    .build();

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendVerifyFailUrl))
                    .build();
        }
    }

    @Override
    @GetMapping("/email/status")
    public ResponseEntity<?> getEmailVerificationStatus(
            @RequestParam("email") String email) {
        return ResponseEntity.ok(authService.getEmailVerificationStatus(email));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpReqDto dto) {
        return ResponseEntity.ok(authService.signup(dto));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}