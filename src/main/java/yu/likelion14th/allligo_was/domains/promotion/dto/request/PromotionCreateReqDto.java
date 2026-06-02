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
@Schema(description = "홍보 콘텐츠 생성 요청 DTO")
public class PromotionCreateReqDto {

    // BLOG, VIDEO
    @Schema(description = "생성할 콘텐츠 타입입니다. BLOG 또는 VIDEO만 입력할 수 있습니다.", example = "VIDEO")
    @NotBlank(message = "콘텐츠 타입을 선택해주세요.")
    private String contentType;

    @Schema(description = "홍보물 제목입니다. 3자 이상 20자 이하로 입력합니다.", example = "딸기라떼숏츠홍보")
    @NotBlank(message = "홍보 제목을 입력해주세요.")
    @Size(min = 3, max = 20, message = "홍보 제목은 3자 이상 20자 이하로 입력해주세요.")
    private String promotionTitle;

    @Schema(description = "홍보 콘텐츠 생성을 위한 추가 프롬프트입니다. 최대 250자까지 입력할 수 있습니다.", example = "신메뉴 딸기라떼를 인스타그램 숏츠 느낌으로 홍보해줘")
    @NotBlank(message = "추가 프롬프트를 입력해주세요.")
    @Size(max = 250, message = "추가 프롬프트는 250자 이하로 입력해주세요.")
    private String prompt;

    @Schema(description = "날씨 정보를 콘텐츠 생성에 반영할지 여부입니다.", example = "false")
    @NotNull(message = "날씨 정보 사용 여부를 선택해주세요.")
    private Boolean weatherEnabled;

    @Schema(description = "콘텐츠 분위기 태그입니다. 따뜻함, 차분함, 밝음 중 하나만 입력할 수 있습니다.", example = "밝음")
    @NotBlank(message = "분위기 태그를 선택해주세요.")
    private String mode;

    @Schema(description = "홍보 요청의 마감일입니다. 마감일 이후에는 스케줄 실행 대상에서 제외됩니다.", example = "2026-06-30T23:59:00")
    @NotNull(message = "마감일을 입력해주세요.")
    private LocalDateTime deadline;

    @Schema(description = "홍보에 사용할 이미지 URL 목록입니다. 1장 이상 5장 이하로 등록할 수 있습니다.", example = "[\"https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg\", \"https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image2.jpg\"]")
    @NotEmpty(message = "이미지를 1장 이상 등록해주세요.")
    @Size(min = 1, max = 5, message = "이미지는 1장 이상 5장 이하로 등록할 수 있습니다.")
    private List<String> imageUrls;

    @Schema(description = "홍보 해시태그 목록입니다. 10개 미만으로 등록할 수 있으며, 각 태그는 7자 이하입니다.", example = "[\"딸기라떼\", \"신메뉴\", \"카페\"]")
    @Size(max = 9, message = "해시태그는 10개 미만으로 등록할 수 있습니다.")
    private List<@Size(max = 7, message = "해시태그는 7자 이하로 입력해주세요.") String> tags;

    @Schema(description = "홍보 배포 스케줄 목록입니다. 1개 이상 등록해야 합니다.")
    @Valid
    @NotEmpty(message = "스케줄을 1개 이상 등록해주세요.")
    private List<PromotionScheduleReqDto> schedules;
}