package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastapiContentResponseDto {
    private String taskId;
    private String status;
    private String message;
}
