package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastapiShortformResponseDto {
    private String videoUrl;
    private Integer imageCount;
    private String marketingText;
    private Double durationSeconds;
    private Long createdAtMillis;
}
