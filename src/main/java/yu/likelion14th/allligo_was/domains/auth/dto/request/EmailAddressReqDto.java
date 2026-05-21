package yu.likelion14th.allligo_was.domains.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailAddressReqDto {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @Email(message = "이메일 형식을 맞추어 작성해주세요.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;
}