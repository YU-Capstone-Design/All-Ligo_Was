package yu.likelion14th.allligo_was.domains.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "생성 완료 콘텐츠 미리보기 응답 DTO")
public class ContentPreviewResDto {

    @Schema(description = "콘텐츠 ID", example = "5")
    private Long contentId;

    @Schema(description = "콘텐츠 실행 ID", example = "1")
    private Long executionId;

    @Schema(description = "홍보 요청 ID", example = "3")
    private Long promotionId;

    @Schema(description = "게시글 제목", example = "오늘의 따뜻한 커피 이벤트")
    private String promotionTitle;

    @Schema(description = "콘텐츠 타입", example = "VIDEO")
    private String contentType;

    @Schema(description = "콘텐츠 타입 한글 표시명", example = "영상")
    private String contentTypeLabel;

    @Schema(description = "콘텐츠 상태", example = "GENERATED")
    private String status;

    @Schema(description = "포스트 또는 영상 썸네일 이미지 URL", example = "https://all-ligo-bucket.s3.ap-northeast-2.amazonaws.com/poster/example.png")
    private String posterUrl;

    @Schema(description = "블로그/포스트 본문 텍스트", example = "오늘 비 오는 날에 어울리는 따뜻한 커피를 준비했습니다.")
    private String bodyText;

    @Schema(description = "숏츠/영상용 캡션", example = "비 오는 날엔 따뜻한 라떼 한 잔 어떠세요?")
    private String caption;

    @Schema(description = "S3에 저장된 영상 URL", example = "https://all-ligo-bucket.s3.ap-northeast-2.amazonaws.com/videos/example.mp4")
    private String s3VideoUrl;

    @Schema(description = "서버 내부 로컬 영상 저장 경로", example = "/tmp/all-ligo/videos/example.mp4")
    private String localVideoPath;

    @Schema(description = "플랫폼 업로드 후 생성된 영상 URL", example = "https://youtube.com/shorts/example")
    private String uploadVideoUrl;

    @Schema(description = "콘텐츠 생성 시간", example = "2026-06-02T17:50:00")
    private LocalDateTime createdAt;

    @Schema(description = "콘텐츠 만료 시간", example = "2026-06-03T17:50:00")
    private LocalDateTime expiresAt;

    @Schema(description = "플랫폼 업로드 완료 시간. 업로드 전이면 null입니다.", example = "2026-06-02T18:00:00")
    private LocalDateTime uploadedAt;

    public static ContentPreviewResDto fromEntity(Content content) {
        Promotion promotion = content.getPromotionExecution().getPromotion();

        return ContentPreviewResDto.builder()
                .contentId(content.getContentId())
                .executionId(content.getPromotionExecution().getExecutionId())
                .promotionId(promotion.getPromotionId())
                .promotionTitle(promotion.getPromotionTitle())
                .contentType(content.getContentType())
                .contentTypeLabel(toContentTypeLabel(content.getContentType()))
                .status(content.getStatus())
                .posterUrl(content.getPosterUrl())
                .bodyText(content.getBodyText())
                .caption(content.getCaption())
                .s3VideoUrl(content.getS3VideoUrl())
                .localVideoPath(content.getLocalVideoPath())
                .uploadVideoUrl(content.getUploadVideoUrl())
                .createdAt(content.getCreatedAt())
                .expiresAt(content.getExpiresAt())
                .uploadedAt(content.getUploadedAt())
                .build();
    }

    private static String toContentTypeLabel(String contentType) {
        if ("VIDEO".equals(contentType)) {
            return "영상";
        }

        return "텍스트";
    }
}