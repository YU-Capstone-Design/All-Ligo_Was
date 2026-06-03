package yu.likelion14th.allligo_was.domains.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StoreCouponResponseDto {
    private Long storeId;
    private String storeName;
    private String region;
    private Double latitude;
    private Double longitude;
    private String mapUrl;
    private String profileImageUrl;
    private List<CouponInfoResponseDto> coupons;

    public static StoreCouponResponseDto of(Store store, List<CouponInfoResponseDto> coupons) {
        return StoreCouponResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .region(store.getRegion())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .mapUrl(store.getMapUrl())
                .profileImageUrl(store.getProfileImageUrl())
                .coupons(coupons)
                .build();
    }
}
