package yu.likelion14th.allligo_was.fastapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ResponseEntity;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiWebhookDto;
import yu.likelion14th.allligo_was.fastapi.service.ContentCallbackService;
import yu.likelion14th.allligo_was.fastapi.service.ContentCallbackService.WebhookResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentCallbackControllerTest {

    private final ContentCallbackService contentCallbackService = mock(ContentCallbackService.class);
    private final ContentCallbackController controller = new ContentCallbackController(contentCallbackService);

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({"PROCESSED, 200", "INVALID, 400", "EXECUTION_NOT_FOUND, 503"})
    @DisplayName("웹훅 처리 결과를 Agent 재시도 정책에 맞는 응답 코드로 돌려준다")
    void mapsWebhookResultToStatus(WebhookResult result, int expectedStatus) {
        when(contentCallbackService.processWebhook(any())).thenReturn(result);

        ResponseEntity<String> response = controller.handleContentCallback(FastapiWebhookDto.builder().taskId("task-1").build());

        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }
}
