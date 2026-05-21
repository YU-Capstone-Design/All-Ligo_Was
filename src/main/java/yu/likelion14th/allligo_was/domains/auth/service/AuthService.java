package yu.likelion14th.allligo_was.domains.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.auth.dto.response.EmailCheckResDto;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

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

    private void validateEmailFormat(String email) {
        if (email == null || email.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_FORMAT);
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!email.matches(emailRegex)) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }
}