package yu.likelion14th.allligo_was.domains.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yu.likelion14th.allligo_was.domains.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}