package yu.likelion14th.allligo_was.fastapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastapiWebhookDto {
    private String taskId;
    private String scheduleId;
    private String status;
    private String jobType;
    private String error;
    private WebhookData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebhookData {
        private String scheduleId;
        private String generatedText;
        private String generatedImageUrl;
        private String generatedVideoUrl;
        private String s3VideoUrl;
        private String localVideoPath;
        private String uploadSchedule;
        private Long createdAtMillis;
    }
}
