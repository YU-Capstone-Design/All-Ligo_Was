package yu.likelion14th.allligo_was.domains.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.coupon.entity.DiscountType;

@Getter
@Builder
public class CouponResDto {

    private Long couponId;
    private String imageUrl;
    private String menuName;
    private Integer discountNum;
    private DiscountType discountType;
    private String message;
}