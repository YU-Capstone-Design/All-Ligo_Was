package yu.likelion14th.allligo_was.domains.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.domains.user.dto.request.UserProfileUpdateReqDto;
import yu.likelion14th.allligo_was.domains.user.dto.response.UserMypageResDto;
import yu.likelion14th.allligo_was.domains.user.entity.User;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public UserMypageResDto getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return UserMypageResDto.builder()
                .email(user.getEmail())
                .storeName(store.getStoreName())
                .mapUrl(store.getMapUrl())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .profileImageUrl(store.getProfileImageUrl())
                .build();
    }

    @Transactional
    public UserMypageResDto updateProfile(Long userId, UserProfileUpdateReqDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        validateLink(dto.getMapUrl());

        store.updateProfile(
                dto.getStoreName(),
                dto.getMapUrl(),
                dto.getLatitude(),
                dto.getLongitude()
        );

        return UserMypageResDto.builder()
                .email(user.getEmail())
                .storeName(store.getStoreName())
                .mapUrl(store.getMapUrl())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .profileImageUrl(store.getProfileImageUrl())
                .build();
    }

    private void validateLink(String mapUrl) {
        if (mapUrl == null || mapUrl.isBlank()
                || (!mapUrl.startsWith("http://") && !mapUrl.startsWith("https://"))) {
            throw new CustomException(ErrorCode.INVALID_LINK);
        }
    }
}