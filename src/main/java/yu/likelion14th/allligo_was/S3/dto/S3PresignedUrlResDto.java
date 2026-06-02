package yu.likelion14th.allligo_was.S3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class S3PresignedUrlResDto {

    @Schema(
            description = "S3 PUT 업로드에 사용할 Presigned URL입니다. 프론트는 이 URL로 직접 파일을 업로드합니다.",
            example = "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/1/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
    )
    private String presignedUrl;

    @Schema(
            description = "DB에 저장할 최종 파일 URL입니다. 업로드 성공 후 각 도메인 API에 이 값을 전달합니다.",
            example = "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/1/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png"
    )
    private String fileUrl;

    public static S3PresignedUrlResDto of(String presignedUrl, String fileUrl) {
        return S3PresignedUrlResDto.builder()
                .presignedUrl(presignedUrl)
                .fileUrl(fileUrl)
                .build();
    }
}