package yu.likelion14th.allligo_was.domains.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserProfileUpdateReqDto {

    @NotBlank(message = "가게 이름은 필수 입력입니다.")
    private String storeName;

    @NotBlank(message = "링크는 필수 입력입니다.")
    private String mapUrl;

    @NotNull(message = "위도는 필수 입력입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수 입력입니다.")
    private Double longitude;
}