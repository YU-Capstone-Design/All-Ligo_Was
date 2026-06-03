package yu.likelion14th.allligo_was.domains.promotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "홍보 요청 전체 수정 DTO")
public class PromotionUpdateReqDto {

    @Schema(description = "수정할 홍보물 제목입니다. 3자 이상 20자 이하로 입력합니다.", example = "수정된커피홍보")
    @NotBlank(message = "홍보 제목을 입력해주세요.")
    @Size(min = 3, max = 20, message = "홍보 제목은 3자 이상 20자 이하로 입력해주세요.")
    private String promotionTitle;

    // POST, VIDEO
    @Schema(description = "수정할 콘텐츠 타입입니다. POST 또는 VIDEO만 입력할 수 있습니다.", example = "POST")
    @NotBlank(message = "콘텐츠 타입을 선택해주세요.")
    private String contentType;

    @Schema(description = "수정할 추가 프롬프트입니다. 최대 250자까지 입력할 수 있습니다.", example = "비 오는 날 방문 고객을 대상으로 따뜻한 라떼 할인 이벤트를 홍보하는 글을 만들어줘")
    @NotBlank(message = "추가 프롬프트를 입력해주세요.")
    @Size(max = 250, message = "추가 프롬프트는 250자 이하로 입력해주세요.")
    private String prompt;

    @Schema(description = "날씨 정보를 콘텐츠 생성에 반영할지 여부입니다.", example = "true")
    @NotNull(message = "날씨 정보 사용 여부를 선택해주세요.")
    private Boolean weatherEnabled;

    @Schema(description = "수정할 콘텐츠 분위기 태그입니다. 따뜻함, 차분함, 밝음 중 하나만 입력할 수 있습니다.", example = "차분함")
    @NotBlank(message = "분위기 태그를 선택해주세요.")
    private String mode;

    @Schema(description = "수정할 홍보 요청의 마감일입니다.", example = "2026-07-10T23:59:00")
    @NotNull(message = "마감일을 입력해주세요.")
    private LocalDateTime deadline;

    @Schema(description = "수정할 이미지 URL 목록입니다. 기존 이미지는 전체 삭제되고 요청값으로 다시 저장됩니다.", example = "[\"https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg\"]")
    @NotEmpty(message = "이미지를 1장 이상 등록해주세요.")
    @Size(min = 1, max = 5, message = "이미지는 1장 이상 5장 이하로 등록할 수 있습니다.")
    private List<String> imageUrls;

    @Schema(description = "수정할 해시태그 목록입니다. 기존 태그는 전체 삭제되고 요청값으로 다시 저장됩니다.", example = "[\"라떼\", \"할인\", \"이벤트\"]")
    @Size(max = 9, message = "해시태그는 10개 미만으로 등록할 수 있습니다.")
    private List<@Size(max = 7, message = "해시태그는 7자 이하로 입력해주세요.") String> tags;

    @Schema(description = "수정할 배포 스케줄 목록입니다. 기존 스케줄은 전체 삭제되고 요청값으로 다시 저장됩니다.")
    @Valid
    @NotEmpty(message = "스케줄을 1개 이상 등록해주세요.")
    private List<PromotionScheduleReqDto> schedules;
}