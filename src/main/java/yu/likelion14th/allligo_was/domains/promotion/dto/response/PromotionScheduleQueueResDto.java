package yu.likelion14th.allligo_was.domains.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "스케줄링 대기열 조회 응답 DTO")
public class PromotionScheduleQueueResDto {

    @Schema(description = "실행 ID", example = "1")
    private Long executionId;

    @Schema(description = "홍보 요청 ID", example = "3")
    private Long promotionId;

    @Schema(description = "생성된 콘텐츠 ID. 아직 생성 전이면 null입니다.", example = "5")
    private Long contentId;

    @Schema(description = "게시글 제목", example = "오늘의 따뜻한 커피 이벤트")
    private String promotionTitle;

    @Schema(description = "콘텐츠 타입", example = "VIDEO")
    private String contentType;

    @Schema(description = "콘텐츠 타입 한글 표시명", example = "영상")
    private String contentTypeLabel;

    @Schema(description = "대기열 상태 코드", example = "GENERATED")
    private String status;

    @Schema(description = "대기열 상태 한글 표시명", example = "생성 완료")
    private String statusLabel;

    @Schema(description = "콘텐츠 생성 또는 실행 예정 시간", example = "2026-06-02T18:00:00")
    private LocalDateTime executedAt;

    @Schema(description = "콘텐츠 생성 시간. 생성 전이면 null입니다.", example = "2026-06-02T17:50:00")
    private LocalDateTime createdAt;

    @Schema(description = "콘텐츠 만료 시간. 생성 전이면 null입니다.", example = "2026-06-03T17:50:00")
    private LocalDateTime expiresAt;

    @Schema(description = "미리보기 페이지 이동 가능 여부", example = "true")
    private boolean clickable;

    public static PromotionScheduleQueueResDto fromEntity(
            PromotionExecution execution,
            String status,
            String statusLabel,
            boolean clickable
    ) {
        Promotion promotion = execution.getPromotion();
        Content content = execution.getContent();

        String contentType = promotion.getContentType();

        return PromotionScheduleQueueResDto.builder()
                .executionId(execution.getExecutionId())
                .promotionId(promotion.getPromotionId())
                .contentId(content != null ? content.getContentId() : null)
                .promotionTitle(promotion.getPromotionTitle())
                .contentType(contentType)
                .contentTypeLabel(toContentTypeLabel(contentType))
                .status(status)
                .statusLabel(statusLabel)
                .executedAt(execution.getExecutedAt())
                .createdAt(content != null ? content.getCreatedAt() : null)
                .expiresAt(content != null ? content.getExpiresAt() : null)
                .clickable(clickable)
                .build();
    }

    private static String toContentTypeLabel(String contentType) {
        if ("VIDEO".equals(contentType)) {
            return "영상";
        }

        return "텍스트";
    }
}