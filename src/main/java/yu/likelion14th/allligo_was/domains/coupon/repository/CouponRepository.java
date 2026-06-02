package yu.likelion14th.allligo_was.domains.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.coupon.entity.Coupon;
import yu.likelion14th.allligo_was.domains.store.entity.Store;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findAllByStore(Store store);
}