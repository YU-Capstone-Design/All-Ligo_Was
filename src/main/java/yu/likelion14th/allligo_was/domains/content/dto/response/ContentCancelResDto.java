package yu.likelion14th.allligo_was.domains.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.content.entity.Content;

@Getter
@Builder
@Schema(description = "생성 콘텐츠 배포 중단 응답 DTO")
public class ContentCancelResDto {

    @Schema(description = "콘텐츠 ID", example = "5")
    private Long contentId;

    @Schema(description = "변경된 콘텐츠 상태", example = "CANCELLED")
    private String status;

    @Schema(description = "처리 결과 메시지", example = "콘텐츠 배포가 중단되었습니다.")
    private String message;

    public static ContentCancelResDto fromEntity(Content content) {
        return ContentCancelResDto.builder()
                .contentId(content.getContentId())
                .status(content.getStatus())
                .message("콘텐츠 배포가 중단되었습니다.")
                .build();
    }
}