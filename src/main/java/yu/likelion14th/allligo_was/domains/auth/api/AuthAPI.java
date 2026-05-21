package yu.likelion14th.allligo_was.domains.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auth API", description = "회원가입, 로그인, 이메일 인증 관련 API입니다.")
public interface AuthAPI {

    @Operation(
            summary = "이메일 중복 확인",
            description = "회원가입 시 입력한 이메일이 이미 등록된 이메일인지 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "이메일 중복 확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "사용 가능한 이메일",
                                            value = """
                                                    {
                                                      "available": true,
                                                      "message": "사용 가능한 이메일입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 등록된 이메일",
                                            value = """
                                                    {
                                                      "available": false,
                                                      "message": "이미 등록된 메일은 사용할 수 없어요."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<?> checkEmail(
            @Parameter(description = "중복 확인할 이메일", example = "test@example.com")
            @RequestParam("email") String email
    );
}