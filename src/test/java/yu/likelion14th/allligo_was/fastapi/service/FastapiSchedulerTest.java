package yu.likelion14th.allligo_was.fastapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yu.likelion14th.allligo_was.domains.content.entity.Content;
import yu.likelion14th.allligo_was.domains.content.entity.ContentStatus;
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionImageRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionTagRepository;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private FastapiScheduler fastapiScheduler;

    private final PromotionExecution execution = PromotionExecution.builder()
            .executionId(1L)
            .executedAt(LocalDateTime.now())
            .status("SUCCESS")
            .build();

    @Test
    @DisplayName("Track B: 배포 중단된 콘텐츠는 유튜브에 업로드하지 않는다")
    void skipsUploadForCancelledContent() {
        Content content = videoContent(ContentStatus.CANCELLED);
        when(executionRepository.findAllByExecutedAtBetween(any(), any())).thenReturn(List.of(execution));
        when(contentRepository.findByPromotionExecution(execution)).thenReturn(Optional.of(content));

        fastapiScheduler.executeTwoTrackScheduler();

        verify(fastapiClientService, never()).uploadToYoutube(any());
        assertThat(content.getStatus()).isEqualTo(ContentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Track B: 생성 완료된 콘텐츠는 업로드 후 PUBLISHED 로 바꾼다")
    void uploadsGeneratedContent() {
        Content content = videoContent(ContentStatus.GENERATED);
        when(executionRepository.findAllByExecutedAtBetween(any(), any())).thenReturn(List.of(execution));
        when(contentRepository.findByPromotionExecution(execution)).thenReturn(Optional.of(content));
        when(fastapiClientService.uploadToYoutube(any())).thenReturn(FastapiUploadResponseDto.builder()
                .status("SUCCESS")
                .youtubeUrl("https://youtu.be/abc")
                .build());

        fastapiScheduler.executeTwoTrackScheduler();

        assertThat(content.getStatus()).isEqualTo(ContentStatus.PUBLISHED);
        assertThat(content.getUploadVideoUrl()).isEqualTo("https://youtu.be/abc");
    }

    private Content videoContent(String status) {
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
