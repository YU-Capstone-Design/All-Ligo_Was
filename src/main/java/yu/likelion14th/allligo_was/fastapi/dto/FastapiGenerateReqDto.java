package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastapiGenerateReqDto {
    @JsonProperty("moodTag")
    private String moodTag;       // 분위기태그
    
    @JsonProperty("hashTag")
    private String hashTag;       // 해시태그
    
    private String prompt;        // 프롬프트
    
    @JsonProperty("uploadDay")
    private String uploadDay;     // 업로드 요일 선택
    
    @JsonProperty("uploadTime")
    private String uploadTime;    // 업로드 시간 선택
    
    @JsonProperty("scheduleId")
    private String scheduleId;      // 스케줄 ID (Webhook 반환용)
    
    private Double lat;           // (선택) 위도
    private Double lon;           // (선택) 경도
    
    @Builder.Default
    @JsonProperty("contentType")
    private String contentType = "IMAGE";   // "POST" (블로그/인스타) 또는 "VIDEO" (숏폼 영상)
    
    @Builder.Default
    private String mode = "TRANSFORM";          // "TRANSFORM" (AI 변형) 또는 "ORIGINAL" (원본 유지)
    
    @Builder.Default
    @JsonProperty("imageUrls")
    private List<String> imageUrls = new ArrayList<>(); // S3 이미지 링크 리스트
    
    @JsonProperty("topPerformers")
    private String topPerformers; // JSON 직렬화된 과거 우수 성과 데이터 문자열
}
