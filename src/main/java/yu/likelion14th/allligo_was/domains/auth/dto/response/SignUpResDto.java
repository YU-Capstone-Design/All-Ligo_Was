package yu.likelion14th.allligo_was.domains.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResDto {

    @Schema(description = "생성된 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "생성된 가게 ID", example = "1")
    private Long storeId;

    @Schema(description = "응답 메시지", example = "회원가입이 완료되었습니다.")
    private String message;
}