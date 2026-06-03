package yu.likelion14th.allligo_was.domains.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    private static final Set<String> ALLOWED_REGIONS = new HashSet<>(Arrays.asList(
            "서울", "인천", "경기", "강원", "대전", "세종", "충남", "충북",
            "대구", "경북", "부산", "울산", "경남", "광주", "전남", "전북", "제주"
    ));

    public List<Store> getStoresByRegion(String region) {
        validateRegion(region);
        return storeRepository.findAllByRegionWithCoupons(region);
    }

    public List<Store> getNearbyStores(Double latitude, Double longitude) {
        return storeRepository.findNearbyStores(latitude, longitude);
    }

    private void validateRegion(String region) {
        if (region == null || !ALLOWED_REGIONS.contains(region)) {
            throw new IllegalArgumentException("올바르지 않은 행정구역입니다.");
        }
    }
}
