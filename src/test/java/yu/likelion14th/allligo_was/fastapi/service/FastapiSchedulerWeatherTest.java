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
import yu.likelion14th.allligo_was.domains.content.repository.ContentRepository;
import yu.likelion14th.allligo_was.domains.promotion.entity.Promotion;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionExecution;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionExecutionRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionImageRepository;
import yu.likelion14th.allligo_was.domains.promotion.repository.PromotionTagRepository;
import yu.likelion14th.allligo_was.domains.store.entity.Store;
import yu.likelion14th.allligo_was.domains.store.repository.StoreRepository;
import yu.likelion14th.allligo_was.domains.user.entity.User;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FastapiSchedulerWeatherTest {

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
    @DisplayName("날씨 반영을 켠 홍보는 매장 좌표를 lat/lon 으로 보낸다")
    void sendsStoreLocationWhenWeatherEnabled() {
        givenPendingExecution(promotion(true));
        when(storeRepository.findFirstByUserUserIdOrderByStoreIdAsc(5L)).thenReturn(Optional.of(store(35.8361, 128.7523)));

        FastapiGenerateReqDto request = requestSent();

        assertThat(request.getLat()).isEqualTo(35.8361);
        assertThat(request.getLon()).isEqualTo(128.7523);
    }

    @Test
    @DisplayName("날씨 반영을 끈 홍보는 좌표를 보내지 않는다")
    void omitsLocationWhenWeatherDisabled() {
        givenPendingExecution(promotion(false));

        FastapiGenerateReqDto request = requestSent();

        assertThat(request.getLat()).isNull();
        assertThat(request.getLon()).isNull();
        verify(storeRepository, never()).findFirstByUserUserIdOrderByStoreIdAsc(anyLong());
    }

    @Test
    @DisplayName("매장 좌표가 0,0 이면 보내지 않는다 (엉뚱한 지역 날씨 방지)")
    void omitsZeroLocation() {
        givenPendingExecution(promotion(true));
        when(storeRepository.findFirstByUserUserIdOrderByStoreIdAsc(5L)).thenReturn(Optional.of(store(0.0, 0.0)));

        FastapiGenerateReqDto request = requestSent();

        assertThat(request.getLat()).isNull();
        assertThat(request.getLon()).isNull();
    }

    private void givenPendingExecution(Promotion promotion) {
        PromotionExecution execution = PromotionExecution.builder()
                .executionId(1L)
                .executedAt(LocalDateTime.now().plusMinutes(5))
                .status("PENDING")
                .promotion(promotion)
                .build();
        when(executionRepository.findAllByStatusAndExecutedAtBetween(eq("PENDING"), any(), any())).thenReturn(List.of(execution));
        when(executionRepository.findById(1L)).thenReturn(Optional.of(execution));
        when(fastapiClientService.generateContent(any()))
                .thenReturn(new FastapiContentResponseDto("task-1", "PROCESSING", "Background task started"));
    }

    private FastapiGenerateReqDto requestSent() {
        fastapiScheduler.executeTwoTrackScheduler();
        ArgumentCaptor<FastapiGenerateReqDto> captor = ArgumentCaptor.forClass(FastapiGenerateReqDto.class);
        verify(fastapiClientService).generateContent(captor.capture());
        return captor.getValue();
    }

    private Promotion promotion(boolean weatherEnabled) {
        return Promotion.builder()
                .promotionId(3L)
                .contentType("VIDEO")
                .prompt("시그니처 라떼를 소개해 주세요")
                .mode("밝음")
                .isWeatherEnabled(weatherEnabled)
                .user(User.builder().userId(5L).build())
                .build();
    }

    private Store store(double latitude, double longitude) {
        return Store.builder().storeId(1L).latitude(latitude).longitude(longitude).build();
    }
}
