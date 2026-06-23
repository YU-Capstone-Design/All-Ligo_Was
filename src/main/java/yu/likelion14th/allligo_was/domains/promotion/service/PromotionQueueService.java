package yu.likelion14th.allligo_was.domains.promotion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionScheduleQueueResDto;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionQueueService {

    private static final String EXECUTION_STATUS_PENDING = "PENDING";
    private static final String EXECUTION_STATUS_PROCESSING = "PROCESSING";
    private static final String EXECUTION_STATUS_SUCCESS = "SUCCESS";
    private static final String EXECUTION_STATUS_FAILED = "FAILED";

    private static final String CONTENT_STATUS_GENERATED = "GENERATED";
    private static final String CONTENT_STATUS_PUBLISHED = "PUBLISHED";
    private static final String CONTENT_STATUS_CANCELLED = "CANCELLED";

    private final PromotionExecutionRepository promotionExecutionRepository;

    /**
     * 로그인한 사용자의 24시간 이내 콘텐츠 스케줄링 대기열을 조회합니다.
     *
     * <p>
     * 현재 시각부터 24시간 이내 실행 예정 또는 실행된 PromotionExecution을 조회합니다.
     * PENDING, PROCESSING, FAILED 상태는 실행 상태 기준으로 응답하고,
     * SUCCESS 상태는 연결된 Content 상태가 GENERATED인 경우에만 응답합니다.
     * PUBLISHED, CANCELLED 상태의 콘텐츠는 대기열에서 제외합니다.
     * </p>
     *
     * @param userId 현재 로그인한 사용자 ID
     * @return 24시간 이내 스케줄링 대기열 응답 목록
     */
    public List<PromotionScheduleQueueResDto> getScheduleQueue(Long userId) {
        LocalDateTime now = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = now.plusHours(24);

        List<PromotionExecution> executions =
                promotionExecutionRepository.findQueueByUserId(userId, now, endTime);

        return executions.stream()
                .map(this::toQueueResponse)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * PromotionExecution 엔티티를 대기열 응답 DTO로 변환합니다.
     *
     * <p>
     * 실행 상태가 SUCCESS인 경우에는 연결된 Content의 상태를 추가로 확인합니다.
     * 생성 완료 상태인 GENERATED만 미리보기 가능한 항목으로 응답하고,
     * PUBLISHED와 CANCELLED는 대기열에서 제외하기 위해 null을 반환합니다.
     * </p>
     *
     * @param execution 홍보 실행 엔티티
     * @return 대기열 응답 DTO 또는 제외 대상인 경우 null
     */
    private PromotionScheduleQueueResDto toQueueResponse(PromotionExecution execution) {
        String executionStatus = execution.getStatus();

        if (EXECUTION_STATUS_PENDING.equals(executionStatus)) {
            return PromotionScheduleQueueResDto.fromEntity(
                    execution,
                    EXECUTION_STATUS_PENDING,
                    "대기중",
                    false
            );
        }

        if (EXECUTION_STATUS_PROCESSING.equals(executionStatus)) {
            return PromotionScheduleQueueResDto.fromEntity(
                    execution,
                    EXECUTION_STATUS_PROCESSING,
                    "생성중",
                    false
            );
        }

        if (EXECUTION_STATUS_FAILED.equals(executionStatus)) {
            return PromotionScheduleQueueResDto.fromEntity(
                    execution,
                    EXECUTION_STATUS_FAILED,
                    "실패",
                    false
            );
        }

        if (EXECUTION_STATUS_SUCCESS.equals(executionStatus)) {
            return toSuccessQueueResponse(execution);
        }

        return null;
    }

    /**
     * SUCCESS 상태의 실행 결과를 Content 상태 기준으로 대기열 응답 DTO로 변환합니다.
     *
     * @param execution SUCCESS 상태의 홍보 실행 엔티티
     * @return 생성 완료 응답 DTO 또는 대기열 제외 대상인 경우 null
     */
    private PromotionScheduleQueueResDto toSuccessQueueResponse(PromotionExecution execution) {
        Content content = execution.getContent();

        if (content == null) {
            return null;
        }

        String contentStatus = content.getStatus();

        if (CONTENT_STATUS_GENERATED.equals(contentStatus)) {
            return PromotionScheduleQueueResDto.fromEntity(
                    execution,
                    CONTENT_STATUS_GENERATED,
                    "생성 완료",
                    true
            );
        }

        if (CONTENT_STATUS_PUBLISHED.equals(contentStatus)) {
            return null;
        }

        if (CONTENT_STATUS_CANCELLED.equals(contentStatus)) {
            return null;
        }

        return null;
    }
}