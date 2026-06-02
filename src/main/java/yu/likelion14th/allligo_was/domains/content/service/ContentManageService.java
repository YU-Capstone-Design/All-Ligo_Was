package yu.likelion14th.allligo_was.domains.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.content.dto.response.ContentCancelResDto;
import yu.likelion14th.allligo_was.domains.content.dto.response.ContentPreviewResDto;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.exception.CustomException;
import yu.likelion14th.allligo_was.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class ContentManageService {

    private static final String STATUS_GENERATED = "GENERATED";
    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final ContentRepository contentRepository;

    /**
     * 생성 완료된 콘텐츠의 미리보기 정보를 조회합니다.
     *
     * <p>
     * 로그인한 사용자가 소유한 콘텐츠만 조회할 수 있으며,
     * 콘텐츠 상태가 GENERATED인 경우에만 미리보기를 허용합니다.
     * </p>
     *
     * @param userId 현재 로그인한 사용자 ID
     * @param contentId 조회할 콘텐츠 ID
     * @return 생성 완료 콘텐츠 미리보기 응답 DTO
     */
    @Transactional(readOnly = true)
    public ContentPreviewResDto getContentPreview(Long userId, Long contentId) {
        Content content = getMyContent(userId, contentId);

        validatePreviewAvailable(content);

        return ContentPreviewResDto.fromEntity(content);
    }

    /**
     * 생성 완료된 콘텐츠의 배포를 중단합니다.
     *
     * <p>
     * 해당 기능은 스케줄링 자체를 삭제하는 기능이 아니라,
     * 이미 생성된 특정 Content의 상태만 CANCELLED로 변경하는 기능입니다.
     * 따라서 Promotion, PromotionSchedule, PromotionExecution 데이터는 그대로 유지됩니다.
     * </p>
     *
     * @param userId 현재 로그인한 사용자 ID
     * @param contentId 배포 중단할 콘텐츠 ID
     * @return 배포 중단 결과 응답 DTO
     */
    @Transactional
    public ContentCancelResDto cancelContent(Long userId, Long contentId) {
        Content content = getMyContent(userId, contentId);

        validateCancelAvailable(content);

        content.setStatus(STATUS_CANCELLED);

        return ContentCancelResDto.fromEntity(content);
    }

    /**
     * 현재 로그인한 사용자의 콘텐츠를 조회합니다.
     *
     * <p>
     * contentId와 userId를 함께 조건으로 조회하여
     * 다른 사용자의 콘텐츠에 접근하지 못하도록 제한합니다.
     * </p>
     *
     * @param userId 현재 로그인한 사용자 ID
     * @param contentId 조회할 콘텐츠 ID
     * @return 현재 사용자가 소유한 콘텐츠 엔티티
     */
    private Content getMyContent(Long userId, Long contentId) {
        return contentRepository
                .findByContentIdAndPromotionExecutionPromotionUserUserId(contentId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
    }

    /**
     * 콘텐츠 미리보기 가능 여부를 검증합니다.
     *
     * @param content 검증할 콘텐츠 엔티티
     */
    private void validatePreviewAvailable(Content content) {
        if (!STATUS_GENERATED.equals(content.getStatus())) {
            throw new CustomException(ErrorCode.CONTENT_PREVIEW_NOT_ALLOWED);
        }
    }

    /**
     * 콘텐츠 배포 중단 가능 여부를 검증합니다.
     *
     * <p>
     * GENERATED 상태의 콘텐츠만 배포 중단할 수 있습니다.
     * 이미 발행된 PUBLISHED 상태이거나 이미 취소된 CANCELLED 상태인 경우에는 예외를 발생시킵니다.
     * </p>
     *
     * @param content 검증할 콘텐츠 엔티티
     */
    private void validateCancelAvailable(Content content) {
        if (STATUS_CANCELLED.equals(content.getStatus())) {
            throw new CustomException(ErrorCode.CONTENT_ALREADY_CANCELLED);
        }

        if (STATUS_PUBLISHED.equals(content.getStatus())) {
            throw new CustomException(ErrorCode.CONTENT_ALREADY_PUBLISHED);
        }

        if (!STATUS_GENERATED.equals(content.getStatus())) {
            throw new CustomException(ErrorCode.CONTENT_CANCEL_NOT_ALLOWED);
        }
    }
}