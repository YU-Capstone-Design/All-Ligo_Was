package yu.likelion14th.allligo_was.domains.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "홍보 요청 목록 응답 DTO")
public class PromotionListResDto {

    @Schema(description = "홍보 요청 ID", example = "1")
    private Long promotionId;

    // 홍보물 제목
    @Schema(description = "홍보물 제목", example = "비오는날커피홍보")
    private String promotionTitle;

    // 첫 번째 홍보 이미지
    @Schema(description = "목록 카드에 표시할 첫 번째 홍보 이미지 URL", example = "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg")
    private String thumbnailImageUrl;

    // 홍보물 등록 날짜
    @Schema(description = "홍보 요청 등록 시각", example = "2026-06-02T20:40:00")
    private LocalDateTime createdAt;

    // BLOG, VIDEO
    @Schema(description = "콘텐츠 타입", example = "BLOG")
    private String contentType;

    public static PromotionListResDto fromEntity(
            Promotion promotion,
            String thumbnailImageUrl
    ) {
        return PromotionListResDto.builder()
                .promotionId(promotion.getPromotionId())
                .promotionTitle(promotion.getPromotionTitle())
                .thumbnailImageUrl(thumbnailImageUrl)
                .createdAt(promotion.getCreatedAt())
                .contentType(promotion.getContentType())
                .build();
    }
}