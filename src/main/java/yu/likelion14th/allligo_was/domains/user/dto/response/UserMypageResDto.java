package yu.likelion14th.allligo_was.domains.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMypageResDto {

    private String email;
    private String storeName;
    private String mapUrl;
    private Double latitude;
    private Double longitude;
    private String profileImageUrl;
}