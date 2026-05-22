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
import yu.likelion14th.allligo_was.domains.auth.dto.request.SignUpReqDto;

@Tag(name = "Auth API", description = "회원가입, 로그인, 이메일 인증 관련 API입니다.")
public interface AuthAPI {

    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 입력한 이메일의 형식을 검증하고, 이미 등록된 이메일인지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "available": true,
                      "message": "사용 가능한 이메일입니다."
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "message": "이메일 형식을 맞추어 작성해주세요."
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 이메일", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 409,
                      "message": "이미 등록된 메일은 사용할 수 없어요."
                    }
                    """)))
    })
    ResponseEntity<?> checkEmail(
            @Parameter(description = "중복 확인할 이메일", example = "test@example.com") @RequestParam("email") String email);

    @Operation(summary = "이메일 인증 메일 발송", description = "회원가입 시 입력한 이메일로 인증 메일을 발송합니다. 메일의 인증하기 버튼은 백엔드 인증 API를 호출하며, 인증 링크는 5분 동안 유효합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 메일 발송 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "message": "인증 메일이 발송되었습니다.",
                      "expiresInMinutes": 5
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "message": "이메일 형식을 맞추어 작성해주세요."
                    }
                    """))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 이메일", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 409,
                      "message": "이미 등록된 메일은 사용할 수 없어요."
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "인증 메일 발송 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 500,
                      "message": "인증 메일 발송에 실패했습니다."
                    }
                    """)))
    })
    ResponseEntity<?> sendVerificationEmail(
            @Valid @RequestBody EmailAddressReqDto dto);

    @Operation(summary = "이메일 인증 완료", description = "인증 메일 링크의 email과 token을 검증하여 이메일 인증을 완료합니다. 인증 성공 시 프론트의 인증 완료 안내 화면으로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "이메일 인증 완료 후 프론트 인증 완료 안내 화면으로 리다이렉트"),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류 / 토큰 오류 / 인증 시간 만료", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "토큰 오류", value = """
                            {
                              "status": 400,
                              "message": "인증 토큰이 올바르지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "인증 시간 만료", value = """
                            {
                              "status": 400,
                              "message": "인증 시간이 만료되었습니다."
                            }
                            """)
            }))
    })
    ResponseEntity<Void> verifyEmail(
            @Parameter(description = "인증할 이메일", example = "test@example.com") @RequestParam("email") String email,

            @Parameter(description = "이메일 인증 토큰", example = "550e8400-e29b-41d4-a716-446655440000") @RequestParam("token") String token);

    @Operation(summary = "이메일 인증 상태 확인", description = "회원가입 화면에서 완료 버튼을 눌렀을 때 해당 이메일의 인증 완료 여부를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 상태 조회 성공", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "인증 완료", value = """
                            {
                              "email": "test@example.com",
                              "verified": true
                            }
                            """),
                    @ExampleObject(name = "인증 미완료", value = """
                            {
                              "email": "test@example.com",
                              "verified": false
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "message": "이메일 형식을 맞추어 작성해주세요."
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "해당 이메일 인증 정보 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "message": "해당 이메일 인증 정보를 찾을 수 없습니다."
                    }
                    """)))
    })
    ResponseEntity<?> getEmailVerificationStatus(
            @Parameter(description = "인증 상태를 확인할 이메일", example = "test@example.com") @RequestParam("email") String email);

    @Operation(summary = "소상공인 회원가입", description = "이메일 인증이 완료된 사용자의 계정과 가게 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "userId": 1,
                      "storeId": 1,
                      "message": "회원가입이 완료되었습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 / 비밀번호 오류 / 이메일 미인증"),
            @ApiResponse(responseCode = "404", description = "이메일 인증 정보 없음"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 이메일")
    })
    ResponseEntity<?> signup(
            @Valid @RequestBody SignUpReqDto dto);
}