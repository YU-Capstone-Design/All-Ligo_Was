package yu.likelion14th.allligo_was.fastapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiWebhookDto;
import yu.likelion14th.allligo_was.fastapi.service.ContentCallbackService;

@Slf4j
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class ContentCallbackController {

    private final ContentCallbackService contentCallbackService;

    @PostMapping("/content-callback")
    public ResponseEntity<String> handleContentCallback(@RequestBody FastapiWebhookDto payload) {
        log.info("Webhook received from FastAPI. Task ID: {}", payload.getTaskId());
        contentCallbackService.processWebhook(payload);
        return ResponseEntity.ok("Webhook processed");
    }
}
