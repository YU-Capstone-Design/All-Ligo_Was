package yu.likelion14th.allligo_was.domains.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponCreateReqDto;
import yu.likelion14th.allligo_was.domains.coupon.dto.response.CouponResDto;
import yu.likelion14th.allligo_was.domains.coupon.entity.Coupon;
import yu.likelion14th.allligo_was.domains.coupon.repository.CouponRepository;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.domains.user.entity.User;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;
import yu.likelion14th.allligo_was.domains.coupon.dto.response.CouponListResDto;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponUpdateReqDto;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

        private final CouponRepository couponRepository;
        private final UserRepository userRepository;
        private final StoreRepository storeRepository;

        @Transactional
        public CouponResDto createCoupon(Long userId, CouponCreateReqDto dto) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                Store store = storeRepository.findByUser(user)
                                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

                validateImageUrl(dto.getImageUrl());

                Coupon coupon = Coupon.builder()
                                .imageUrl(dto.getImageUrl())
                                .menuName(dto.getMenuName())
                                .discountNum(dto.getDiscountNum())
                                .discountType(dto.getDiscountType())
                                .createdAt(LocalDateTime.now())
                                .store(store)
                                .build();

                Coupon savedCoupon = couponRepository.save(coupon);

                return CouponResDto.builder()
                                .couponId(savedCoupon.getCouponId())
                                .imageUrl(savedCoupon.getImageUrl())
                                .menuName(savedCoupon.getMenuName())
                                .discountNum(savedCoupon.getDiscountNum())
                                .discountType(savedCoupon.getDiscountType())
                                .message("쿠폰이 등록되었습니다.")
                                .build();
        }

        private void validateImageUrl(String imageUrl) {
                if (imageUrl == null || imageUrl.isBlank()
                                || (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://"))) {
                        throw new CustomException(ErrorCode.INVALID_COUPON_IMAGE_URL);
                }
        }

        public List<CouponListResDto> getMyCoupons(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                Store store = storeRepository.findByUser(user)
                                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

                return couponRepository.findAllByStore(store)
                                .stream()
                                .map(CouponListResDto::fromEntity)
                                .toList();
        }

        @Transactional
        public CouponResDto updateCoupon(Long userId, Long couponId, CouponUpdateReqDto dto) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                Store store = storeRepository.findByUser(user)
                                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

                Coupon coupon = couponRepository.findById(couponId)
                                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

                validateCouponOwner(coupon, store);
                validateImageUrl(dto.getImageUrl());

                coupon.updateCoupon(
                                dto.getImageUrl(),
                                dto.getMenuName(),
                                dto.getDiscountNum(),
                                dto.getDiscountType());

                return CouponResDto.builder()
                                .couponId(coupon.getCouponId())
                                .imageUrl(coupon.getImageUrl())
                                .menuName(coupon.getMenuName())
                                .discountNum(coupon.getDiscountNum())
                                .discountType(coupon.getDiscountType())
                                .message("쿠폰이 수정되었습니다.")
                                .build();
        }

        @Transactional
        public CouponResDto deleteCoupon(Long userId, Long couponId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                Store store = storeRepository.findByUser(user)
                                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

                Coupon coupon = couponRepository.findById(couponId)
                                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

                validateCouponOwner(coupon, store);

                couponRepository.delete(coupon);

                return CouponResDto.builder()
                                .couponId(coupon.getCouponId())
                                .imageUrl(coupon.getImageUrl())
                                .menuName(coupon.getMenuName())
                                .discountNum(coupon.getDiscountNum())
                                .discountType(coupon.getDiscountType())
                                .message("쿠폰이 삭제되었습니다.")
                                .build();
        }

        private void validateCouponOwner(Coupon coupon, Store store) {
                if (!coupon.getStore().getStoreId().equals(store.getStoreId())) {
                        throw new CustomException(ErrorCode.FORBIDDEN_COUPON_ACCESS);
                }
        }
}