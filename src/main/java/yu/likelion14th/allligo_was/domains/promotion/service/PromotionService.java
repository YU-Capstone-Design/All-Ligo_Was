package yu.likelion14th.allligo_was.domains.promotion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.S3.dto.UploadDomain;
import yu.likelion14th.allligo_was.S3.service.S3Service;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionCreateReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionScheduleReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionUpdateReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionDetailResDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionListResDto;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionImage;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionTag;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionImageRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionScheduleRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionTagRepository;
import yu.likelion14th.allligo_was.domains.user.entity.User;
import yu.likelion14th.allligo_was.domains.user.repository.UserRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;


import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService {

    private static final List<String> ALLOWED_PROMOTION_MODES =
            List.of("따뜻함", "차분함", "밝음");

    private static final List<String> ALLOWED_DAYS_OF_WEEK =
            List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

    private final PromotionRepository promotionRepository;
    private final PromotionImageRepository promotionImageRepository;
    private final PromotionTagRepository promotionTagRepository;
    private final PromotionScheduleRepository promotionScheduleRepository;
    private final PromotionExecutionRepository promotionExecutionRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    /**
     * 홍보 콘텐츠 생성 요청 및 스케줄을 등록합니다.
     *
     * @param userId  요청 사용자 ID
     * @param request 홍보 생성 요청 DTO
     * @return 생성된 홍보 요청 상세 정보
     */
    public PromotionDetailResDto createPromotion(Long userId, PromotionCreateReqDto request) {
        validateCreateRequest(request);
        s3Service.validateFileUrls(userId, UploadDomain.PROMOTION, request.getImageUrls());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        Promotion promotion = Promotion.builder()
                .promotionTitle(request.getPromotionTitle())
                .contentType(request.getContentType())
                .prompt(request.getPrompt())
                .isWeatherEnabled(Boolean.TRUE.equals(request.getWeatherEnabled()))
                .mode(request.getMode())
                .deadline(request.getDeadline())
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .build();

        Promotion savedPromotion = promotionRepository.save(promotion);

        List<PromotionImage> savedImages = saveImages(savedPromotion, request.getImageUrls());
        List<PromotionTag> savedTags = saveTags(savedPromotion, request.getTags());
        List<PromotionSchedule> savedSchedules = saveSchedules(savedPromotion, request.getSchedules());

        return PromotionDetailResDto.fromEntity(
                savedPromotion,
                savedImages,
                savedTags,
                savedSchedules
        );
    }

    /**
     * 현재 로그인한 사용자의 홍보 요청 목록을 조회합니다.
     *
     * @param userId 조회 사용자 ID
     * @return 홍보 요청 목록
     */
    @Transactional(readOnly = true)
    public List<PromotionListResDto> getMyPromotions(Long userId) {
        return promotionRepository.findAllByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(promotion -> {
                    String thumbnailImageUrl = promotionImageRepository
                            .findFirstByPromotionPromotionIdOrderByImageIdAsc(promotion.getPromotionId())
                            .map(PromotionImage::getImageUrl)
                            .orElse(null);

                    return PromotionListResDto.fromEntity(promotion, thumbnailImageUrl);
                })
                .toList();
    }

    /**
     * 특정 홍보 요청의 상세 정보를 조회합니다.
     *
     * @param userId      조회 사용자 ID
     * @param promotionId 조회할 홍보 요청 ID
     * @return 홍보 요청 상세 정보
     */
    @Transactional(readOnly = true)
    public PromotionDetailResDto getPromotionDetail(Long userId, Long promotionId) {
        Promotion promotion = getPromotionByUser(userId, promotionId);

        List<PromotionImage> images =
                promotionImageRepository.findAllByPromotionPromotionId(promotionId);

        List<PromotionTag> tags =
                promotionTagRepository.findAllByPromotionPromotionId(promotionId);

        List<PromotionSchedule> schedules =
                promotionScheduleRepository.findAllByPromotionPromotionId(promotionId);

        return PromotionDetailResDto.fromEntity(promotion, images, tags, schedules);
    }

    /**
     * 특정 홍보 요청 전체 정보를 수정합니다.
     *
     * 수정 대상:
     * 1. promotion 기본 정보
     * 2. promotion_image
     * 3. promotion_tag
     * 4. promotion_schedule
     *
     * 이미지, 태그, 스케줄은 기존 데이터를 전체 삭제한 뒤 새 데이터로 다시 저장합니다.
     *
     * @param userId      수정 요청 사용자 ID
     * @param promotionId 수정할 홍보 요청 ID
     * @param request     홍보 수정 요청 DTO
     * @return 수정된 홍보 요청 상세 정보
     */
    public PromotionDetailResDto updatePromotion(
            Long userId,
            Long promotionId,
            PromotionUpdateReqDto request
    ) {
        Promotion promotion = getPromotionByUser(userId, promotionId);

        validateUpdateRequest(request);
        s3Service.validateFileUrls(userId, UploadDomain.PROMOTION, request.getImageUrls());

        promotion.updatePromotionInfo(
                request.getPromotionTitle(),
                request.getContentType(),
                request.getPrompt(),
                Boolean.TRUE.equals(request.getWeatherEnabled()),
                request.getMode(),
                request.getDeadline()
        );

        promotionImageRepository.deleteAllByPromotionPromotionId(promotionId);
        promotionTagRepository.deleteAllByPromotionPromotionId(promotionId);
        promotionScheduleRepository.deleteAllByPromotionPromotionId(promotionId);

        List<PromotionImage> savedImages = saveImages(promotion, request.getImageUrls());
        List<PromotionTag> savedTags = saveTags(promotion, request.getTags());
        List<PromotionSchedule> savedSchedules = saveSchedules(promotion, request.getSchedules());

        return PromotionDetailResDto.fromEntity(
                promotion,
                savedImages,
                savedTags,
                savedSchedules
        );
    }

    /**
     * 특정 홍보 요청을 삭제합니다.
     *
     * 연결된 데이터 삭제 순서:
     * 1. content
     * 2. promotion_execution
     * 3. promotion_schedule
     * 4. promotion_image
     * 5. promotion_tag
     * 6. promotion
     *
     * @param userId      삭제 요청 사용자 ID
     * @param promotionId 삭제할 홍보 요청 ID
     */
    public void deletePromotion(Long userId, Long promotionId) {
        Promotion promotion = getPromotionByUser(userId, promotionId);

        contentRepository.deleteAllByPromotionExecutionPromotionPromotionId(promotionId);
        promotionExecutionRepository.deleteAllByPromotionPromotionId(promotionId);
        promotionScheduleRepository.deleteAllByPromotionPromotionId(promotionId);
        promotionImageRepository.deleteAllByPromotionPromotionId(promotionId);
        promotionTagRepository.deleteAllByPromotionPromotionId(promotionId);

        promotionRepository.delete(promotion);
    }

    /**
     * 사용자 소유의 홍보 요청을 조회합니다.
     *
     * 존재하지 않거나 현재 사용자의 홍보 요청이 아니면 PROMOTION_NOT_FOUND를 반환합니다.
     *
     * @param userId      사용자 ID
     * @param promotionId 홍보 요청 ID
     * @return 홍보 요청 엔티티
     */
    private Promotion getPromotionByUser(Long userId, Long promotionId) {
        return promotionRepository.findByPromotionIdAndUserUserId(promotionId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROMOTION_NOT_FOUND));
    }

    /**
     * 홍보 이미지 URL 목록을 저장합니다.
     *
     * @param promotion 저장된 홍보 요청
     * @param imageUrls 이미지 URL 목록
     * @return 저장된 이미지 목록
     */
    private List<PromotionImage> saveImages(Promotion promotion, List<String> imageUrls) {
        List<PromotionImage> images = imageUrls.stream()
                .map(imageUrl -> PromotionImage.builder()
                        .imageUrl(imageUrl)
                        .promotion(promotion)
                        .build())
                .toList();

        return promotionImageRepository.saveAll(images);
    }

    /**
     * 홍보 태그 목록을 저장합니다.
     *
     * @param promotion 저장된 홍보 요청
     * @param tags      태그 목록
     * @return 저장된 태그 목록
     */
    private List<PromotionTag> saveTags(Promotion promotion, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }

        List<PromotionTag> promotionTags = tags.stream()
                .map(tag -> PromotionTag.builder()
                        .tagName(tag)
                        .tagType("USER")
                        .promotion(promotion)
                        .build())
                .toList();

        return promotionTagRepository.saveAll(promotionTags);
    }

    /**
     * 홍보 스케줄 목록을 저장합니다.
     *
     * @param promotion 저장된 홍보 요청
     * @param schedules 스케줄 목록
     * @return 저장된 스케줄 목록
     */
    private List<PromotionSchedule> saveSchedules(Promotion promotion, List<PromotionScheduleReqDto> schedules) {
        List<PromotionSchedule> promotionSchedules = schedules.stream()
                .map(schedule -> PromotionSchedule.builder()
                        .dayOfWeek(schedule.getDayOfWeek())
                        .publishTime(schedule.getPublishTime())
                        .promotion(promotion)
                        .build())
                .toList();

        return promotionScheduleRepository.saveAll(promotionSchedules);
    }

    /**
     * 홍보 생성 요청값을 검증합니다.
     *
     * @param request 홍보 생성 요청 DTO
     */
    private void validateCreateRequest(PromotionCreateReqDto request) {
        if (request.getPromotionTitle() == null
                || request.getPromotionTitle().isBlank()
                || request.getPromotionTitle().length() < 3
                || request.getPromotionTitle().length() > 20) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_TITLE);
        }

        if (!"POST".equals(request.getContentType())
                && !"VIDEO".equals(request.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_CONTENT_TYPE);
        }

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()
                || request.getPrompt().length() > 250) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_PROMPT);
        }

        if (request.getMode() == null
                || request.getMode().isBlank()
                || !ALLOWED_PROMOTION_MODES.contains(request.getMode())) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_MODE);
        }

        if (request.getImageUrls() == null
                || request.getImageUrls().isEmpty()
                || request.getImageUrls().size() > 5) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_IMAGE_COUNT);
        }

        if (request.getTags() != null && request.getTags().size() >= 10) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_TAG_COUNT);
        }

        validateTags(request.getTags());

        if (request.getDeadline() == null
                || request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_DEADLINE);
        }

        if (request.getSchedules() == null || request.getSchedules().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_SCHEDULE);
        }

        validateSchedules(request.getSchedules());
    }

    /**
     * 홍보 수정 요청값을 검증합니다.
     *
     * @param request 홍보 수정 요청 DTO
     */
    private void validateUpdateRequest(PromotionUpdateReqDto request) {
        if (request.getPromotionTitle() == null
                || request.getPromotionTitle().isBlank()
                || request.getPromotionTitle().length() < 3
                || request.getPromotionTitle().length() > 20) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_TITLE);
        }

        if (!"POST".equals(request.getContentType())
                && !"VIDEO".equals(request.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_CONTENT_TYPE);
        }

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()
                || request.getPrompt().length() > 250) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_PROMPT);
        }

        if (request.getMode() == null
                || request.getMode().isBlank()
                || !ALLOWED_PROMOTION_MODES.contains(request.getMode())) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_MODE);
        }

        if (request.getImageUrls() == null
                || request.getImageUrls().isEmpty()
                || request.getImageUrls().size() > 5) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_IMAGE_COUNT);
        }

        if (request.getTags() != null && request.getTags().size() >= 10) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_TAG_COUNT);
        }

        validateTags(request.getTags());

        if (request.getDeadline() == null
                || request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_DEADLINE);
        }

        if (request.getSchedules() == null || request.getSchedules().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PROMOTION_SCHEDULE);
        }

        validateSchedules(request.getSchedules());
    }

    /**
     * 태그 목록을 검증합니다.
     *
     * @param tags 태그 목록
     */
    private void validateTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        Set<String> tagSet = new HashSet<>();

        for (String tag : tags) {
            if (tag == null || tag.isBlank() || tag.length() > 7) {
                throw new CustomException(ErrorCode.INVALID_PROMOTION_TAG_LENGTH);
            }

            String normalizedTag = tag.trim();

            if (!tagSet.add(normalizedTag)) {
                throw new CustomException(ErrorCode.DUPLICATE_PROMOTION_TAG);
            }
        }
    }

    /**
     * 스케줄 목록을 검증합니다.
     *
     * @param schedules 스케줄 목록
     */
    private void validateSchedules(List<PromotionScheduleReqDto> schedules) {
        LocalDateTime minimumPublishTime = LocalDateTime.now().plusHours(1);
        Set<String> scheduleKeys = new HashSet<>();

        for (PromotionScheduleReqDto schedule : schedules) {
            if (schedule.getDayOfWeek() == null
                    || schedule.getDayOfWeek().isBlank()
                    || !ALLOWED_DAYS_OF_WEEK.contains(schedule.getDayOfWeek())) {
                throw new CustomException(ErrorCode.INVALID_PROMOTION_DAY_OF_WEEK);
            }

            if (schedule.getPublishTime() == null) {
                throw new CustomException(ErrorCode.INVALID_PROMOTION_PUBLISH_TIME);
            }

            if (schedule.getPublishTime().isBefore(minimumPublishTime)) {
                throw new CustomException(ErrorCode.INVALID_PROMOTION_PUBLISH_TIME);
            }

            String scheduleKey = schedule.getDayOfWeek() + "_"
                    + schedule.getPublishTime()
                    .toLocalTime()
                    .withSecond(0)
                    .withNano(0);

            if (!scheduleKeys.add(scheduleKey)) {
                throw new CustomException(ErrorCode.DUPLICATE_PROMOTION_SCHEDULE);
            }
        }

    }
}