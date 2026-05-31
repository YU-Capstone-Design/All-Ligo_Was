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
import org.springframework.web.client.RestTemplate;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastapiClientService {

    private final RestTemplate restTemplate;

    @Value("${agent.server.url:http://localhost:8000}")
    private String agentServerUrl;

    public FastapiContentResponseDto generateContent(yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto reqDto) {
        String url = agentServerUrl + "/api/marketing/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto> requestEntity = new HttpEntity<>(reqDto, headers);

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

}
