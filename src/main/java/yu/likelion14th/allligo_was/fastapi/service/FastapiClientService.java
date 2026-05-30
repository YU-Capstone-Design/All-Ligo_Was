package yu.likelion14th.allligo_was.fastapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiShortformResponseDto;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastapiClientService {

    private final RestTemplate restTemplate;

    @Value("${agent.server.url:http://localhost:8000}")
    private String agentServerUrl;

    public FastapiContentResponseDto generateContent(yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto reqDto, MultipartFile image) {
        String url = agentServerUrl + "/api/marketing/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // 변경된 Agent API 요구사항에 맞게 변환
        if (reqDto.getScheduleId() != null) body.add("scheduleId", reqDto.getScheduleId().toString());
        body.add("moodTag", reqDto.getMoodTag() != null ? reqDto.getMoodTag() : "");
        body.add("hashTag", reqDto.getHashTag() != null ? reqDto.getHashTag() : "");
        body.add("prompt", reqDto.getPrompt() != null ? reqDto.getPrompt() : "");
        body.add("uploadDay", reqDto.getUploadDay() != null ? reqDto.getUploadDay() : "");
        body.add("uploadTime", reqDto.getUploadTime() != null ? reqDto.getUploadTime() : "");
        
        if (reqDto.getLat() != null) body.add("lat", reqDto.getLat());
        if (reqDto.getLon() != null) body.add("lon", reqDto.getLon());
        
        if (image != null && !image.isEmpty()) {
            body.add("image", createResource(image));
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            log.info("Sending request to FastAPI: {}", url);
            ResponseEntity<FastapiContentResponseDto> response = restTemplate.postForEntity(url, requestEntity, FastapiContentResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to call FastAPI generateContent API", e);
            throw new RuntimeException("Failed to call FastAPI", e);
        }
    }

    public yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto uploadToYoutube(yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadReqDto reqDto) {
        String url = agentServerUrl + "/api/marketing/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // FastapiUploadReqDto를 JSON 형태로 직접 전송
        HttpEntity<yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadReqDto> requestEntity = new HttpEntity<>(reqDto, headers);

        try {
            log.info("Sending upload request to FastAPI: {}", url);
            ResponseEntity<yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto> response = restTemplate.postForEntity(url, requestEntity, yu.likelion14th.allligo_was.fastapi.dto.FastapiUploadResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to call FastAPI upload API", e);
            throw new RuntimeException("Failed to call FastAPI upload API", e);
        }
    }

    public FastapiShortformResponseDto createShortform(List<MultipartFile> images, String text, Double secondsPerImage) {
        String url = agentServerUrl + "/api/marketing/create-shortform";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("secondsPerImage", secondsPerImage != null ? secondsPerImage : 3.0);
        
        if (images != null && !images.isEmpty()) {
            for (MultipartFile img : images) {
                if (!img.isEmpty()) {
                    body.add("images", createResource(img));
                }
            }
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            log.info("Sending request to FastAPI: {}", url);
            ResponseEntity<FastapiShortformResponseDto> response = restTemplate.postForEntity(url, requestEntity, FastapiShortformResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to call FastAPI createShortform API", e);
            throw new RuntimeException("Failed to call FastAPI", e);
        }
    }

    private Resource createResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
