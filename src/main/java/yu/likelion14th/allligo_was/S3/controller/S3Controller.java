package yu.likelion14th.allligo_was.S3.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.S3.api.S3API;
import yu.likelion14th.allligo_was.S3.dto.S3PresignedUrlReqDto;
import yu.likelion14th.allligo_was.S3.dto.S3PresignedUrlResDto;
import yu.likelion14th.allligo_was.S3.service.S3Service;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class S3Controller implements S3API {

    private final S3Service s3Service;

    @Override
    @PostMapping("/presigned-url")
    public ResponseEntity<S3PresignedUrlResDto> createPresignedUrl(
            @Valid @RequestBody S3PresignedUrlReqDto request
    ) {
        Long userId = getCurrentUserId();

        S3PresignedUrlResDto response = s3Service.createPresignedUrl(
                userId,
                request.getDomain(),
                request.getContentType()
        );

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}