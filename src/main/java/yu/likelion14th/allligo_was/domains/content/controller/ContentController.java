package yu.likelion14th.allligo_was.domains.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.net.URI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yu.likelion14th.allligo_was.domains.content.service.ContentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/track/{contentId}")
    public ResponseEntity<Void> trackAndRedirect(@PathVariable Long contentId){
        // 외부(소상공인) URL로 리다이렉트 응답 생성 (Location 헤더 + 302 Found)
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(contentService.trackAndRedirect(contentId)))
                .build();
    }
}
