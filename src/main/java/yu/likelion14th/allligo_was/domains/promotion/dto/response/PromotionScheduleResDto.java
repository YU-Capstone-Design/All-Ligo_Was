package yu.likelion14th.allligo_was.domains.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yu.likelion14th.allligo_was.domains.promotion.entity.PromotionSchedule;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "홍보 배포 스케줄 응답 DTO")
public class PromotionScheduleResDto {

    @Schema(description = "스케줄 ID", example = "1")
    private Long scheduleId;

    @Schema(description = "배포 요일", example = "FRIDAY")
    private String dayOfWeek;

    @Schema(description = "배포 예정 시각", example = "2026-06-07T18:00:00")
    private LocalDateTime publishTime;

    public static PromotionScheduleResDto fromEntity(PromotionSchedule schedule) {
        return PromotionScheduleResDto.builder()
                .scheduleId(schedule.getScheduleId())
                .dayOfWeek(schedule.getDayOfWeek())
                .publishTime(schedule.getPublishTime())
                .build();
    }
}