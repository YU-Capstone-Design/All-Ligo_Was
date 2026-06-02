package yu.likelion14th.allligo_was.domains.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.net.URI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yu.likelion14th.allligo_was.domains.content.api.ContentAPI;
import yu.likelion14th.allligo_was.domains.content.service.ContentManageService;
import yu.likelion14th.allligo_was.domains.content.service.ContentService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import yu.likelion14th.allligo_was.domains.content.dto.response.ContentCancelResDto;
import yu.likelion14th.allligo_was.domains.content.dto.response.ContentPreviewResDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
public class ContentController implements ContentAPI {

    private final ContentService contentService;
    private final ContentManageService contentManageService;

    @Override
    @GetMapping("/track/{contentId}")
    public ResponseEntity<Void> trackAndRedirect(@PathVariable Long contentId){
        // 외부(소상공인) URL로 리다이렉트 응답 생성 (Location 헤더 + 302 Found)
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(contentService.trackAndRedirect(contentId)))
                .build();
    }

    @Override
    @GetMapping("/{contentId}/preview")
    public ResponseEntity<ContentPreviewResDto> getContentPreview(
            @PathVariable Long contentId
    ) {
        Long userId = getCurrentUserId();

        ContentPreviewResDto response =
                contentManageService.getContentPreview(userId, contentId);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{contentId}/cancel")
    public ResponseEntity<ContentCancelResDto> cancelContent(
            @PathVariable Long contentId
    ) {
        Long userId = getCurrentUserId();

        ContentCancelResDto response =
                contentManageService.cancelContent(userId, contentId);

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
