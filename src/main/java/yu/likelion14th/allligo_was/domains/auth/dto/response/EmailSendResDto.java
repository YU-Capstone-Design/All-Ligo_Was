package yu.likelion14th.allligo_was.domains.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailSendResDto {

    @Schema(description = "응답 메시지", example = "인증 메일이 발송되었습니다.")
    private String message;

    @Schema(description = "인증 만료 시간", example = "5")
    private int expiresInMinutes;
}