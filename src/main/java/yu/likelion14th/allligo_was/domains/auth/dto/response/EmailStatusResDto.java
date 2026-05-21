package yu.likelion14th.allligo_was.domains.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailStatusResDto {

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "이메일 인증 완료 여부", example = "true")
    private boolean verified;
}