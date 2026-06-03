package yu.likelion14th.allligo_was.domains.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.coupon.api.CouponAPI;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponCreateReqDto;
import yu.likelion14th.allligo_was.domains.coupon.service.CouponService;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponUpdateReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponAPI {

    private final CouponService couponService;

    @Override
    @PostMapping
    public ResponseEntity<?> createCoupon(
            @Valid @RequestBody CouponCreateReqDto dto) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(couponService.createCoupon(userId, dto));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<?> getMyCoupons() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(couponService.getMyCoupons(userId));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    @PatchMapping("/{couponId}")
    public ResponseEntity<?> updateCoupon(
            @PathVariable("couponId") Long couponId,
            @Valid @RequestBody CouponUpdateReqDto dto) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(couponService.updateCoupon(userId, couponId, dto));
    }

    @Override
    @DeleteMapping("/{couponId}")
    public ResponseEntity<?> deleteCoupon(
            @PathVariable("couponId") Long couponId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(couponService.deleteCoupon(userId, couponId));
    }

    @Override
    @GetMapping("/region")
    public ResponseEntity<?> getCouponsByRegion(
            @RequestParam("region") String region) {
        return ResponseEntity.ok(couponService.getCouponsByRegion(region));
    }

    @Override
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyCoupons(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {
        return ResponseEntity.ok(couponService.getNearbyCoupons(latitude, longitude));
    }
}