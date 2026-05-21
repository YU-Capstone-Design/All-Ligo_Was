package yu.likelion14th.allligo_was.domains.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailVerifyResDto {

    @Schema(description = "이메일 인증 완료 여부", example = "true")
    private boolean verified;

    @Schema(description = "응답 메시지", example = "이메일 인증이 완료되었습니다.")
    private String message;
}