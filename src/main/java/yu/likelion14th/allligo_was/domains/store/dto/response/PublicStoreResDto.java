package yu.likelion14th.allligo_was.domains.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

@Getter
@Builder
@AllArgsConstructor
public class PublicStoreResDto {

    private Long storeId;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private String address;
    private String profileImageUrl;
    private Long couponCount;
    private Long contentCount;

    public static PublicStoreResDto fromEntity(
            Store store,
            Long couponCount,
            Long contentCount
    ) {
        return PublicStoreResDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .address(store.getRegion())
                .profileImageUrl(store.getProfileImageUrl())
                .couponCount(couponCount)
                .contentCount(contentCount)
                .build();
    }
}
