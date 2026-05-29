package yu.likelion14th.allligo_was.domains.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.coupon.entity.DiscountType;

@Getter
public class CouponCreateReqDto {

    @Schema(description = "쿠폰 이미지 URL", example = "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/example.png")
    @NotBlank(message = "쿠폰 이미지는 필수입니다.")
    private String imageUrl;

    @Schema(description = "메뉴명", example = "아메리카노")
    @NotBlank(message = "메뉴명은 필수 입력입니다.")
    private String menuName;

    @Schema(description = "할인값", example = "1000")
    @NotNull(message = "할인값은 필수 입력입니다.")
    @Positive(message = "할인값은 1 이상이어야 합니다.")
    private Integer discountNum;

    @Schema(description = "할인 타입", example = "AMOUNT")
    @NotNull(message = "할인 타입은 필수 입력입니다.")
    private DiscountType discountType;
}