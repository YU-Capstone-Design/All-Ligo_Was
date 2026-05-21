package yu.likelion14th.allligo_was.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "에러 메시지", example = "이메일 형식을 맞추어 작성해주세요.")
    private String message;
}