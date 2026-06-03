package yu.likelion14th.allligo_was.domains.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserProfileImageUpdateReqDto {

    @Schema(
            description = "프로필 이미지 URL입니다. S3 Presigned URL 발급 API에서 domain을 PROFILE로 요청한 뒤 받은 fileUrl을 전달합니다.",
            example = "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/profile/3/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png"
    )
    @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
    private String profileImageUrl;
}