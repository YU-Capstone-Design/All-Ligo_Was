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
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        FastapiContentResponseDto result = fastapiClientService.generateContent(reqDto, image);
        return ResponseEntity.ok(result);
    }

}
