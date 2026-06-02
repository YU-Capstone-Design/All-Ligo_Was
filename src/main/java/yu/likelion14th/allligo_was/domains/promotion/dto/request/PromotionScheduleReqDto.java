package yu.likelion14th.allligo_was.domains.promotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "홍보 배포 스케줄 요청 DTO")
public class PromotionScheduleReqDto {

    @Schema(description = "배포 요일입니다. MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY 중 하나를 입력합니다.", example = "FRIDAY")
    @NotBlank(message = "배포 요일을 입력해주세요.")
    private String dayOfWeek;

    @Schema(description = "배포 예정 시각입니다. 현재 시간보다 1시간 이후여야 합니다.", example = "2026-06-07T18:00:00")
    @NotNull(message = "배포 시간을 입력해주세요.")
    private LocalDateTime publishTime;
}