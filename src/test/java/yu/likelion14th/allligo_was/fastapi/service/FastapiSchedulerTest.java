package yu.likelion14th.allligo_was.fastapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.entity.ContentStatus;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionImageRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionTagRepository;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FastapiSchedulerTest {

    @Mock
    private FastapiClientService fastapiClientService;
    @Mock
    private PromotionExecutionRepository executionRepository;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private PromotionTagRepository promotionTagRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private PromotionImageRepository promotionImageRepository;
    @Spy
    private TransactionTemplate transactionTemplate = new TransactionTemplate(mock(PlatformTransactionManager.class));

    @InjectMocks
    private FastapiScheduler fastapiScheduler;

    @Test
    @DisplayName("Track A: 생성 요청 후 taskId 만 갱신하고 실행 엔티티를 통째로 다시 저장하지 않는다")
    void storesOnlyTaskIdAfterGenerationRequest() {
        PromotionExecution execution = execution("PENDING");
        when(executionRepository.findAllByStatusAndExecutedAtBetween(eq("PENDING"), any(), any())).thenReturn(List.of(execution));
        when(executionRepository.findById(1L)).thenReturn(Optional.of(execution));
        when(fastapiClientService.generateContent(any()))
                .thenReturn(new FastapiContentResponseDto("task-9", "PROCESSING", "Background task started"));

        fastapiScheduler.executeTwoTrackScheduler();

        assertThat(execution.getStatus()).isEqualTo("PROCESSING");
        verify(executionRepository).updateTaskId(1L, "task-9");
        verify(executionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Track A: 생성 요청이 실패하면 아직 PROCESSING 인 경우에만 FAILED 로 바꾼다")
    void marksFailedOnlyIfStillProcessing() {
        PromotionExecution execution = execution("PENDING");
        when(executionRepository.findAllByStatusAndExecutedAtBetween(eq("PENDING"), any(), any())).thenReturn(List.of(execution));
        when(executionRepository.findById(1L)).thenReturn(Optional.of(execution));
        when(fastapiClientService.generateContent(any())).thenThrow(new RuntimeException("Failed to call FastAPI"));

        fastapiScheduler.executeTwoTrackScheduler();

        verify(executionRepository).markFailedIfProcessing(1L, "Failed to call FastAPI");
    }

    @Test
    @DisplayName("Track A: 다시 읽었을 때 이미 PENDING 이 아니면 생성 요청을 보내지 않는다")
    void skipsExecutionNoLongerPending() {
        PromotionExecution execution = execution("PENDING");
        when(executionRepository.findAllByStatusAndExecutedAtBetween(eq("PENDING"), any(), any())).thenReturn(List.of(execution));
        when(executionRepository.findById(1L)).thenReturn(Optional.of(execution("SUCCESS")));

        fastapiScheduler.executeTwoTrackScheduler();

        verify(fastapiClientService, never()).generateContent(any());
    }

    @Test
    @DisplayName("Track B: 배포 중단된 콘텐츠는 유튜브에 업로드하지 않는다")
    void skipsUploadForCancelledContent() {
        PromotionExecution execution = execution("SUCCESS");
        Content content = videoContent(execution, ContentStatus.CANCELLED);
        when(executionRepository.findUploadTargetIds(any(), any())).thenReturn(List.of(1L));
        when(executionRepository.findById(1L)).thenReturn(Optional.of(execution));
        when(contentRepository.findByPromotionExecution(execution)).thenReturn(Optional.of(content));

        fastapiScheduler.executeTwoTrackScheduler();

        verify(fastapiClientService, never()).uploadToYoutube(any());
        assertThat(content.getStatus()).isEqualTo(ContentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Track B: 생성 완료된 콘텐츠는 업로드 후 PUBLISHED 로 바꾼다")
    void uploadsGeneratedContent() {
        PromotionExecution execution = execution("SUCCESS");
        Content content = videoContent(execution, ContentStatus.GENERATED);
        when(executionRepository.findUploadTargetIds(any(), any())).thenReturn(List.of(1L));
        when(executionRepository.findById(1L)).thenReturn(Optional.of(execution));
        when(contentRepository.findByPromotionExecution(execution)).thenReturn(Optional.of(content));
        when(contentRepository.findById(10L)).thenReturn(Optional.of(content));
        when(fastapiClientService.uploadToYoutube(any())).thenReturn(FastapiUploadResponseDto.builder()
                .status("SUCCESS")
                .youtubeUrl("https://youtu.be/abc")
                .build());

        fastapiScheduler.executeTwoTrackScheduler();

        assertThat(content.getStatus()).isEqualTo(ContentStatus.PUBLISHED);
        assertThat(content.getUploadVideoUrl()).isEqualTo("https://youtu.be/abc");
    }

    @Test
    @DisplayName("생성 요청은 예약 5분 전 회차뿐 아니라 지연 허용 시간(10분) 안의 놓친 회차도 대상으로 한다")
    void generationWindowCoversMissedExecutions() {
        fastapiScheduler.executeTwoTrackScheduler();

        ArgumentCaptor<LocalDateTime> from = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> to = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(executionRepository).findAllByStatusAndExecutedAtBetween(eq("PENDING"), from.capture(), to.capture());
        // to = now + 5분 59초, from = now - 10분
        assertThat(Duration.between(from.getValue(), to.getValue())).isEqualTo(Duration.ofMinutes(15).plusSeconds(59));
    }

    @Test
    @DisplayName("업로드는 예약 시각부터 지연 허용 시간(10분) 동안 매분 재시도 대상으로 조회한다")
    void uploadWindowCoversRetries() {
        fastapiScheduler.executeTwoTrackScheduler();

        ArgumentCaptor<LocalDateTime> from = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> to = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(executionRepository).findUploadTargetIds(from.capture(), to.capture());
        // to = now + 59초, from = now - 10분
        assertThat(Duration.between(from.getValue(), to.getValue())).isEqualTo(Duration.ofMinutes(10).plusSeconds(59));
    }

    @Test
    @DisplayName("지연 허용 시간을 넘긴 PENDING·PROCESSING·미업로드 실행을 같은 기준 시각으로 FAILED 정리한다")
    void expiresOverdueExecutions() {
        fastapiScheduler.executeTwoTrackScheduler();

        ArgumentCaptor<LocalDateTime> pendingLimit = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> processingLimit = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> uploadLimit = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> generationFrom = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(executionRepository).expireByStatus(eq("PENDING"), any(), pendingLimit.capture(), any());
        verify(executionRepository).expireByStatus(eq("PROCESSING"), any(), processingLimit.capture(), any());
        verify(executionRepository).expireNotUploaded(any(), uploadLimit.capture(), any());
        verify(executionRepository).findAllByStatusAndExecutedAtBetween(eq("PENDING"), generationFrom.capture(), any());
        // 정리 기준(이 시각 이전은 FAILED) = 생성 요청 구간의 시작
        assertThat(pendingLimit.getValue()).isEqualTo(generationFrom.getValue());
        assertThat(processingLimit.getValue()).isEqualTo(generationFrom.getValue());
        assertThat(uploadLimit.getValue()).isEqualTo(generationFrom.getValue());
    }

    private PromotionExecution execution(String status) {
        return PromotionExecution.builder()
                .executionId(1L)
                .executedAt(LocalDateTime.now())
                .status(status)
                .build();
    }

    private Content videoContent(PromotionExecution execution, String status) {
        return Content.builder()
                .contentId(10L)
                .promotionExecution(execution)
                .contentType("VIDEO")
                .caption("오늘의 라떼")
                .localVideoPath("static/videos/shortform_1.mp4")
                .status(status)
                .build();
    }
}
