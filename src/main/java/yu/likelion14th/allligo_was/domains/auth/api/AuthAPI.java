package yu.likelion14th.allligo_was.domains.auth.api;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import yu.likelion14th.allligo_was.domains.auth.dto.request.EmailAddressReqDto;

@Tag(name = "Auth API", description = "회원가입, 로그인, 이메일 인증 관련 API입니다.")
public interface AuthAPI {

    @Operation(
            summary = "이메일 중복 확인",
            description = "회원가입 시 입력한 이메일의 형식을 검증하고, 이미 등록된 이메일인지 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용 가능한 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "available": true,
                                              "message": "사용 가능한 이메일입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "이메일 형식을 맞추어 작성해주세요."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 등록된 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "message": "이미 등록된 메일은 사용할 수 없어요."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> checkEmail(
            @Parameter(description = "중복 확인할 이메일", example = "test@example.com")
            @RequestParam("email") String email
    );

    @Operation(
            summary = "이메일 인증 메일 발송",
            description = "회원가입 시 입력한 이메일로 인증 메일을 발송합니다. 인증 메일은 5분 동안 유효합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 메일 발송 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "인증 메일이 발송되었습니다.",
                                              "expiresInMinutes": 5
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "이메일 형식을 맞추어 작성해주세요."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 등록된 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "message": "이미 등록된 메일은 사용할 수 없어요."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "인증 메일 발송 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "인증 메일 발송에 실패했습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> sendVerificationEmail(
        @Valid @RequestBody EmailAddressReqDto dto
    );
}