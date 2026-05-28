package yu.likelion14th.allligo_was.domains.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import yu.likelion14th.allligo_was.domains.user.dto.request.UserProfileUpdateReqDto;

@Tag(name = "User API", description = "마이페이지 및 사용자 정보 관련 API입니다.")
public interface UserAPI {

    @Operation(
            summary = "마이페이지 정보 조회",
            description = "로그인한 소상공인의 이메일, 가게 이름, 지도 링크, 위치 정보, 프로필 이미지 URL을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "마이페이지 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "owner@example.com",
                                              "storeName": "낭만돼지",
                                              "mapUrl": "https://map.naver.com/p/example",
                                              "latitude": 35.836123,
                                              "longitude": 128.752345,
                                              "profileImageUrl": "https://spring.allligo-agent.cloud/images/default-profile.png"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "message": "인증에 실패하였습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 매장 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "매장 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> getMyPage(
            @Parameter(description = "JWT Access Token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader("Authorization") String authorizationHeader
    );

    @Operation(
            summary = "프로필 및 가게 정보 수정",
            description = """
                    로그인한 소상공인의 가게 정보를 수정합니다.
                    수정 가능한 정보는 가게 이름, 지도 링크, 위도, 경도입니다.
                    유효하지 않은 링크 입력 시 "링크가 유효하지 않습니다." 에러 메시지를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "owner@example.com",
                                              "storeName": "수정된 가게 이름",
                                              "mapUrl": "https://map.naver.com/p/updated",
                                              "latitude": 35.837777,
                                              "longitude": 128.753333,
                                              "profileImageUrl": "https://spring.allligo-agent.cloud/images/default-profile.png"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 유효하지 않은 링크",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "링크 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "링크가 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "필수값 누락",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "가게 이름은 필수 입력입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "message": "인증에 실패하였습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 매장 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "매장 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<?> updateProfile(
            @Parameter(description = "JWT Access Token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader("Authorization") String authorizationHeader,

            @Valid @RequestBody UserProfileUpdateReqDto dto
    );
}