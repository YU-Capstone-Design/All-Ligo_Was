package yu.likelion14th.allligo_was.domains.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.coupon.entity.Coupon;
import yu.likelion14th.allligo_was.domains.coupon.entity.DiscountType;

@Getter
@Builder
@AllArgsConstructor
public class CouponInfoResponseDto {
    private Long couponId;
    private String imageUrl;
    private String menuName;
    private Integer discountNum;
    private DiscountType discountType;

    public static CouponInfoResponseDto fromEntity(Coupon coupon) {
        return CouponInfoResponseDto.builder()
                .couponId(coupon.getCouponId())
                .imageUrl(coupon.getImageUrl())
                .menuName(coupon.getMenuName())
                .discountNum(coupon.getDiscountNum())
                .discountType(coupon.getDiscountType())
                .build();
    }
}
