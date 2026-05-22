package yu.likelion14th.allligo_was.domains.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "owner@example.com")
    private String email;

    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "응답 메시지", example = "로그인에 성공했습니다.")
    private String message;
}