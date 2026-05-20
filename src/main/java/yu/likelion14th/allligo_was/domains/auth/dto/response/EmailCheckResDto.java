package yu.likelion14th.allligo_was.domains.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCheckResDto {

    private boolean available;
    private String message;
}