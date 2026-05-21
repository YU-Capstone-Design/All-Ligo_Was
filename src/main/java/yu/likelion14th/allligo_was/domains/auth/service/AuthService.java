package yu.likelion14th.allligo_was.domains.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.auth.dto.request.EmailAddressReqDto;
import yu.likelion14th.allligo_was.domains.auth.dto.response.EmailCheckResDto;
import yu.likelion14th.allligo_was.domains.auth.dto.response.EmailSendResDto;
import yu.likelion14th.allligo_was.domains.auth.entity.Verification;
import yu.likelion14th.allligo_was.domains.auth.repository.VerificationRepository;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;
import yu.likelion14th.allligo_was.domains.auth.dto.response.EmailVerifyResDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final JavaMailSender mailSender;

    @Value("${app.backend.verify-url}")
    private String backendVerifyUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailCheckResDto checkEmailDuplication(String email) {
        validateEmailFormat(email);

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        return EmailCheckResDto.builder()
                .available(true)
                .message("사용 가능한 이메일입니다.")
                .build();
    }

    @Transactional
    public EmailSendResDto sendVerificationEmail(EmailAddressReqDto dto) {
        String email = dto.getEmail();

        validateEmailFormat(email);

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String token = createToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        Verification verification = verificationRepository.findByEmail(email)
                .map(existingVerification -> {
                    existingVerification.updateVerification(token, expiresAt);
                    return existingVerification;
                })
                .orElseGet(() -> Verification.builder()
                        .email(email)
                        .token(token)
                        .isVerified(false)
                        .expiresAt(expiresAt)
                        .createdAt(LocalDateTime.now())
                        .build());

        verificationRepository.save(verification);

        sendMail(email, token);

        return EmailSendResDto.builder()
                .message("인증 메일이 발송되었습니다.")
                .expiresInMinutes(5)
                .build();
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }

    private void sendMail(String toEmail, String token) {
        String verifyLink = backendVerifyUrl + "?email=" + toEmail + "&token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[All-Ligo] 이메일 인증을 완료해주세요.");

            String html = """
                    <!DOCTYPE html>
                    <html lang="ko">
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2>All-Ligo 이메일 인증</h2>
                        <p>아래 버튼을 눌러 이메일 인증을 완료해주세요.</p>
                        <p>인증 링크는 5분 동안 유효합니다.<br></p>
                        <a href="%s"
                           style="display:inline-block; padding:12px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:6px;">
                           이메일 인증하기
                        </a>
                    </body>
                    </html>
                    """.formatted(verifyLink);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private void validateEmailFormat(String email) {
        if (email == null || email.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!email.matches(emailRegex)) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    @Transactional
    public EmailVerifyResDto verifyEmail(String email, String token) {
        validateEmailFormat(email);

    Verification verification = verificationRepository.findByEmailAndToken(email, token)
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_TOKEN_INVALID));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EMAIL_TOKEN_EXPIRED);
        }

        verification.completeVerification();

        return EmailVerifyResDto.builder()
                .verified(true)
                .message("이메일 인증이 완료되었습니다.")
                .build();
    }
}