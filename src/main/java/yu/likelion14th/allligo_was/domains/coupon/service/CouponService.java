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
import yu.likelion14th.allligo_was.S3.dto.UploadDomain;
import yu.likelion14th.allligo_was.S3.service.S3Service;
import yu.likelion14th.allligo_was.domains.coupon.dto.response.StoreCouponResponseDto;
import yu.likelion14th.allligo_was.domains.coupon.dto.response.CouponInfoResponseDto;
import yu.likelion14th.allligo_was.domains.store.service.StoreService;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

        private final CouponRepository couponRepository;
        private final UserRepository userRepository;
        private final StoreRepository storeRepository;
        private final S3Service s3Service;
        private final StoreService storeService;

        @Transactional
        public CouponResDto createCoupon(Long userId, CouponCreateReqDto dto) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                Store store = storeRepository.findByUser(user)
                                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

                s3Service.validateFileUrl(userId, UploadDomain.COUPON, dto.getImageUrl());

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
                s3Service.validateFileUrl(userId, UploadDomain.COUPON, dto.getImageUrl());

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

        public List<CouponInfoResponseDto> getCouponsByStoreId(Long storeId) {
                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

                return couponRepository.findAllByStore(store)
                                .stream()
                                .map(CouponInfoResponseDto::fromEntity)
                                .toList();
        }
}