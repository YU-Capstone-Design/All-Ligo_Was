package yu.likelion14th.allligo_was.fastapi.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiWebhookDto;

@Tag(name = "FastAPI Webhook Callback API (Internal)", description = "FastAPI AI 에이전트가 생성한 콘텐츠 작업 완료 결과를 수신하는 웹훅 엔드포인트입니다. **프론트엔드에서는 호출하거나 신경 쓰실 필요가 없습니다.**")
public interface ContentCallbackAPI {

    @Operation(
        summary = "콘텐츠 생성 완료 결과 수신 웹훅",
        description = "FastAPI AI 에이전트가 이미지/영상 생성을 완료한 후 결과를 Spring WAS로 전송하는 웹훅 콜백 API입니다. 결과를 토대로 DB 내 Execution 상태를 업데이트하고 Content 엔티티를 자동 생성합니다. **[주의] 프론트엔드 연동용 API가 아닙니다.**"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "웹훅 처리 성공",
            content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Webhook processed"))
        )
    })
    ResponseEntity<String> handleContentCallback(
            @RequestBody FastapiWebhookDto payload
    );
}
