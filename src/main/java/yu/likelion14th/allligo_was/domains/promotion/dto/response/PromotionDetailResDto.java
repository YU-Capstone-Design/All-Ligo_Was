package yu.likelion14th.allligo_was.domains.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionImage;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionTag;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "홍보 요청 상세 응답 DTO")
public class PromotionDetailResDto {

    @Schema(description = "홍보 요청 ID", example = "1")
    private Long promotionId;

    @Schema(description = "콘텐츠 타입", example = "POST")
    private String contentType;

    @Schema(description = "홍보물 제목", example = "비오는날커피홍보")
    private String promotionTitle;

    @Schema(description = "홍보 콘텐츠 생성을 위한 추가 프롬프트", example = "비 오는 날 따뜻한 라떼 할인 이벤트를 홍보하는 블로그 글을 만들어줘")
    private String prompt;

    @Schema(description = "날씨 정보 사용 여부", example = "true")
    private Boolean weatherEnabled;

    @Schema(description = "콘텐츠 분위기 태그", example = "따뜻함")
    private String mode;

    @Schema(description = "홍보 요청 마감일", example = "2026-06-30T23:59:00")
    private LocalDateTime deadline;

    @Schema(description = "홍보 요청 등록 시각", example = "2026-06-02T20:40:00")
    private LocalDateTime createdAt;

    @Schema(description = "홍보 요청 수정 시각", example = "2026-06-02T20:40:00")
    private LocalDateTime updatedAt;

    @Schema(description = "홍보 이미지 URL 목록", example = "[\"https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg\", \"https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image2.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "홍보 해시태그 목록", example = "[\"라떼\", \"할인\", \"비오는날\"]")
    private List<String> tags;

    @Schema(description = "홍보 배포 스케줄 목록")
    private List<PromotionScheduleResDto> schedules;

    public static PromotionDetailResDto fromEntity(
            Promotion promotion,
            List<PromotionImage> images,
            List<PromotionTag> tags,
            List<PromotionSchedule> schedules
    ) {
        return PromotionDetailResDto.builder()
                .promotionId(promotion.getPromotionId())
                .contentType(promotion.getContentType())
                .promotionTitle(promotion.getPromotionTitle())
                .prompt(promotion.getPrompt())
                .weatherEnabled(promotion.isWeatherEnabled())
                .mode(promotion.getMode())
                .deadline(promotion.getDeadline())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .imageUrls(
                        images.stream()
                                .map(PromotionImage::getImageUrl)
                                .toList()
                )
                .tags(
                        tags.stream()
                                .map(PromotionTag::getTagName)
                                .toList()
                )
                .schedules(
                        schedules.stream()
                                .map(PromotionScheduleResDto::fromEntity)
                                .toList()
                )
                .build();
    }
}