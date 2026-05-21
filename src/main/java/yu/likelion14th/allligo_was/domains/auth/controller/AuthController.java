package yu.likelion14th.allligo_was.domains.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import yu.likelion14th.allligo_was.domains.auth.api.AuthAPI;
import yu.likelion14th.allligo_was.domains.auth.dto.request.EmailAddressReqDto;
import yu.likelion14th.allligo_was.domains.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthAPI{

    private final AuthService authService;

    @GetMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(authService.checkEmailDuplication(email));
    }

    @PostMapping("/email/send")
    public ResponseEntity<?> sendVerificationEmail(@Valid @RequestBody EmailAddressReqDto dto) {
        return ResponseEntity.ok(authService.sendVerificationEmail(dto));
    }
}