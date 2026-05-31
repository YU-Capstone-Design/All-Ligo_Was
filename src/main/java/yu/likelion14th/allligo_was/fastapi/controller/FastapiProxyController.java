package yu.likelion14th.allligo_was.fastapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto;

import yu.likelion14th.allligo_was.fastapi.service.FastapiClientService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fastapi")
@RequiredArgsConstructor
public class FastapiProxyController {

    private final FastapiClientService fastapiClientService;

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FastapiContentResponseDto> generateContent(
            @ModelAttribute FastapiGenerateReqDto reqDto,
            @RequestParam(value = "images", required = true) List<MultipartFile> images
    ) {
        if (images == null || images.isEmpty() || images.size() > 5) {
            return ResponseEntity.badRequest().build();
        }

        for (MultipartFile img : images) {
            if (img.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }

        // TODO: S3 업로드 로직 추가 및 reqDto.setImageUrls(업로드된_URL_리스트) 설정 필요
        FastapiContentResponseDto result = fastapiClientService.generateContent(reqDto);
        return ResponseEntity.ok(result);
    }

}
