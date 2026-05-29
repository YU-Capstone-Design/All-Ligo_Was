package yu.likelion14th.allligo_was.domains.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.coupon.api.CouponAPI;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponCreateReqDto;
import yu.likelion14th.allligo_was.domains.coupon.service.CouponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponAPI {

    private final CouponService couponService;

    @Override
    @PostMapping
    public ResponseEntity<?> createCoupon(
            @Valid @RequestBody CouponCreateReqDto dto
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(couponService.createCoupon(userId, dto));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}