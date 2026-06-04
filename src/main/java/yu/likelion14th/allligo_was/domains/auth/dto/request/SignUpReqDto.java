package yu.likelion14th.allligo_was.domains.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpReqDto {

    @Schema(description = "사용자 이메일", example = "owner@example.com")
    @Email(message = "이메일 형식을 맞추어 작성해주세요.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    @Schema(description = "가게 이름", example = "낭만돼지")
    @NotBlank(message = "가게 이름은 필수 입력입니다.")
    private String storeName;

    @Schema(description = "네이버 지도 또는 웹사이트 URL", example = "https://map.naver.com/p/example")
    @NotBlank(message = "가게 링크는 필수 입력입니다.")
    private String mapUrl;

    @Schema(description = "가게 위도", example = "35.836123")
    @NotNull(message = "위도는 필수 입력입니다.")
    private Double latitude;

    @Schema(description = "가게 경도", example = "128.752345")
    @NotNull(message = "경도는 필수 입력입니다.")
    private Double longitude;

    @Schema(
            description = "가게 지역 정보입니다. 카카오맵에서 받은 지역명을 전달합니다.",
            example = "대구",
            allowableValues = {
                    "서울", "인천", "경기", "강원",
                    "대전", "세종", "충남", "충북",
                    "대구", "경북", "부산", "울산", "경남",
                    "광주", "전남", "전북", "제주"
            }
    )
    @NotBlank(message = "지역 정보는 필수 입력입니다.")
    private String region;

    @Schema(description = "비밀번호", example = "abc123")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 6, max = 12, message = "6자 이상, 12자 이하로 입력해주세요.")
    private String password;

    @Schema(description = "비밀번호 확인", example = "abc123")
    @NotBlank(message = "비밀번호 확인은 필수 입력입니다.")
    private String passwordConfirm;
}