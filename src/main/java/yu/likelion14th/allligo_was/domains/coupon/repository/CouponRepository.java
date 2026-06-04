package yu.likelion14th.allligo_was.domains.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yu.likelion14th.allligo_was.domains.coupon.entity.Coupon;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findAllByStore(Store store);

    Long countByStore(Store store);

    @Query("select c from Coupon c join fetch c.store where c.store in :stores")
    List<Coupon> findAllByStoreIn(@Param("stores") List<Store> stores);
}