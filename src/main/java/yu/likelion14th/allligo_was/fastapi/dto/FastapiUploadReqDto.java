package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastapiUploadReqDto {
    private Long scheduleId;       // 스케줄 ID
    private String localVideoPath; // FastAPI 서버 내의 비디오 경로 (ex: static/videos/shortform_xxx.mp4)
    private String title;          // 유튜브 영상 제목
    private String description;    // 유튜브 영상 설명
    private List<String> tags;     // 유튜브 영상 태그
}
