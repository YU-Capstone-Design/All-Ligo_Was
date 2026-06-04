package yu.likelion14th.allligo_was.domains.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponseDto {
    private Long storeId;
    private String storeName;
    private String region;
    private Double latitude;
    private Double longitude;
    private String mapUrl;
    private String profileImageUrl;
    private Long couponCount;

    public static StoreResponseDto fromEntity(Store store, Long couponCount) {
        return StoreResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .region(store.getRegion())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .mapUrl(store.getMapUrl())
                .profileImageUrl(store.getProfileImageUrl())
                .couponCount(couponCount)
                .build();
    }
}
