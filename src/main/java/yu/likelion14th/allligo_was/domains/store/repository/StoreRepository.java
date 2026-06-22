package yu.likelion14th.allligo_was.domains.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByUser(User user);

    @Query("select distinct s from Store s join s.coupons where s.region = :region")
    List<Store> findAllByRegionHavingCoupons(@Param("region") String region);

    @Query(value = "SELECT DISTINCT s.* FROM store s " +
                   "INNER JOIN coupon c ON s.store_id = c.store_id " +
                   "WHERE ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(?2, ?1)) <= 3000 " +
                   "ORDER BY ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(?2, ?1)) ASC",
           nativeQuery = true)
    List<Store> findNearbyStoresHavingCoupons(@Param("latitude") Double latitude, @Param("longitude") Double longitude);

    //사용자가 등록한 가게들 중 storeId가 가장 작은 가게 조회
    Optional<Store> findFirstByUserUserIdOrderByStoreIdAsc(Long userId);
}