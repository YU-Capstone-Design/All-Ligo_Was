package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastapiGenerateReqDto {
    private String moodTag;       // 분위기태그
    private String hashTag;       // 해시태그
    private String prompt;        // 프롬프트
    private String uploadDay;     // 업로드 요일 선택
    private String uploadTime;    // 업로드 시간 선택
    private Long scheduleId;      // 스케줄 ID (Webhook 반환용)
    private Double lat;           // (선택) 위도
    private Double lon;           // (선택) 경도
    private String contentType;   // "POST" (블로그/인스타) 또는 "VIDEO" (숏폼 영상)
    private String mode;          // "TRANSFORM" (AI 변형) 또는 "ORIGINAL" (원본 유지)
}
