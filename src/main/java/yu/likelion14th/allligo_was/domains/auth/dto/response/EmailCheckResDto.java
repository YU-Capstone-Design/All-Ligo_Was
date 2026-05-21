package yu.likelion14th.allligo_was.domains.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCheckResDto {

    @Schema(description = "이메일 사용 가능 여부", example = "true")
    private boolean available;

    @Schema(description = "응답 메시지", example = "사용 가능한 이메일입니다.")
    private String message;
}