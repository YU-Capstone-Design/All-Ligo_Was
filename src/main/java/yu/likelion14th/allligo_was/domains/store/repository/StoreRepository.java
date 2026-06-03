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

    @Query("select distinct s from Store s left join fetch s.coupons where s.region = :region")
    List<Store> findAllByRegionWithCoupons(@Param("region") String region);

    @Query(value = "SELECT * FROM store s " +
                   "WHERE ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(?2, ?1)) <= 3000 " +
                   "ORDER BY ST_Distance_Sphere(POINT(s.longitude, s.latitude), POINT(?2, ?1)) ASC",
           nativeQuery = true)
    List<Store> findNearbyStores(@Param("latitude") Double latitude, @Param("longitude") Double longitude);
}