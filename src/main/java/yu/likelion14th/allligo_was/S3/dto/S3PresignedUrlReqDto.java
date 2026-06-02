package yu.likelion14th.allligo_was.S3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class S3PresignedUrlReqDto {

    @Schema(
            description = "업로드 도메인",
            example = "PROMOTION",
            allowableValues = {"PROMOTION", "COUPON", "PROFILE", "CONTENT"}
    )
    @NotNull(message = "업로드 도메인은 필수입니다.")
    private UploadDomain domain;

    @Schema(
            description = "파일 Content-Type",
            example = "image/png",
            allowableValues = {"image/png", "image/jpeg", "image/jpg"}
    )
    @NotBlank(message = "파일 Content-Type은 필수입니다.")
    private String contentType;
}