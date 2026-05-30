package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastapiUploadResponseDto {
    private String status;
    private String scheduleId;
    private String youtubeUrl;
    private String error;
}
