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

    public FastapiContentResponseDto generateContent(String tags, String keywords, String timeSlot, MultipartFile image, Double lat, Double lon) {
        String url = agentServerUrl + "/api/marketing/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("tags", tags);
        body.add("keywords", keywords);
        body.add("timeSlot", timeSlot);
        
        if (lat != null) {
            body.add("lat", lat);
        }
        if (lon != null) {
            body.add("lon", lon);
        }
        
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
