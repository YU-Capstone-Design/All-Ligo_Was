package yu.likelion14th.allligo_was.domains.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.auth.dto.response.EmailCheckResDto;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

    public EmailCheckResDto checkEmailDuplication(String email) {
        boolean exists = userRepository.existsByEmail(email);

        if (exists) {
            return EmailCheckResDto.builder()
                    .available(false)
                    .message("이미 등록된 메일은 사용할 수 없어요.")
                    .build();
        }

        return EmailCheckResDto.builder()
                .available(true)
                .message("사용 가능한 이메일입니다.")
                .build();
    }
}