package yu.likelion14th.allligo_was.fastapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.entity.ContentStatus;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.notification.service.NotificationService;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionScheduleRepository;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiWebhookDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentCallbackServiceTest {

    @Mock
    private PromotionScheduleRepository scheduleRepository;
    @Mock
    private PromotionExecutionRepository executionRepository;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ContentCallbackService contentCallbackService;

    private final PromotionExecution execution = PromotionExecution.builder()
            .executionId(1L)
            .taskId("task-1")
            .status("PROCESSING")
            .build();

    @Test
    @DisplayName("S3 업로드만 실패한 VIDEO 웹훅이어도 localVideoPath 를 저장한다")
    void savesLocalVideoPathWhenS3UploadFailed() {
        when(executionRepository.findByTaskId("task-1")).thenReturn(Optional.of(execution));
        when(contentRepository.findByPromotionExecution(execution)).thenReturn(Optional.empty());
        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> invocation.getArgument(0));

        contentCallbackService.processWebhook(videoWebhook(null, "static/videos/shortform_1.mp4"));

        ArgumentCaptor<Content> saved = ArgumentCaptor.forClass(Content.class);
        verify(contentRepository).save(saved.capture());
        assertThat(saved.getValue().getS3VideoUrl()).isNull();
        assertThat(saved.getValue().getLocalVideoPath()).isEqualTo("static/videos/shortform_1.mp4");
        assertThat(saved.getValue().getStatus()).isEqualTo(ContentStatus.GENERATED);
    }

    @Test
    @DisplayName("배포 중단된 콘텐츠는 중복 웹훅이 와도 GENERATED 로 되돌리지 않는다")
    void ignoresDuplicateWebhookForCancelledContent() {
        Content cancelled = Content.builder()
                .contentId(10L)
                .promotionExecution(execution)
                .contentType("VIDEO")
                .status(ContentStatus.CANCELLED)
                .build();
        when(executionRepository.findByTaskId("task-1")).thenReturn(Optional.of(execution));
        when(contentRepository.findByPromotionExecution(execution)).thenReturn(Optional.of(cancelled));

        contentCallbackService.processWebhook(videoWebhook("https://s3/videos/1.mp4", "static/videos/shortform_1.mp4"));

        assertThat(cancelled.getStatus()).isEqualTo(ContentStatus.CANCELLED);
        verify(contentRepository, never()).save(any(Content.class));
        verify(notificationService, never()).createContentGeneratedNotification(any());
    }

    private FastapiWebhookDto videoWebhook(String s3VideoUrl, String localVideoPath) {
        return FastapiWebhookDto.builder()
                .taskId("task-1")
                .status("SUCCESS")
                .data(FastapiWebhookDto.WebhookData.builder()
                        .contentType("VIDEO")
                        .generatedText("오늘의 라떼")
                        .s3VideoUrl(s3VideoUrl)
                        .localVideoPath(localVideoPath)
                        .build())
                .build();
    }
}
