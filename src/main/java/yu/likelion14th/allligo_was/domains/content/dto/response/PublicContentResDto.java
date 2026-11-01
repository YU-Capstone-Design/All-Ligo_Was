package yu.likelion14th.allligo_was.domains.content.dto.response;

import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;

@Getter
@Builder
public class PublicContentResDto {

    private Long contentId;
    private String contentType;
    private String title;
    private String bodyText;
    private String caption;
    private String posterUrl;
    private String videoUrl;

    public static PublicContentResDto fromEntity(Content content) {

        Promotion promotion = content
                .getPromotionExecution()
                .getPromotion();

        return PublicContentResDto.builder()
                .contentId(content.getContentId())
                .contentType(content.getContentType())
                .title(promotion.getPromotionTitle())
                .bodyText(content.getBodyText())
                .caption(content.getCaption())
                .posterUrl(content.getPosterUrl())
                .videoUrl(
                        "VIDEO".equals(content.getContentType())
                                ? content.getUploadVideoUrl()
                                : null)
                .build();
    }
}