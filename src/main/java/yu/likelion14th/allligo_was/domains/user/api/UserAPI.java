package yu.likelion14th.allligo_was.domains.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import yu.likelion14th.allligo_was.domains.user.dto.request.UserProfileUpdateReqDto;
import yu.likelion14th.allligo_was.domains.user.dto.request.UserProfileImageUpdateReqDto;

@Tag(name = "User API", description = "마이페이지 및 사용자 정보 관련 API입니다.")
public interface UserAPI {

        @Operation(summary = "마이페이지 정보 조회", description = """
                        로그인한 소상공인의 이메일, 가게 이름, 지도 링크, 위치 정보, 프로필 이미지 URL을 조회합니다.
                        Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "email": "owner@example.com",
                                          "storeName": "낭만돼지",
                                          "mapUrl": "https://map.naver.com/p/example",
                                          "latitude": 35.836123,
                                          "longitude": 128.752345,
                                          "profileImageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/default/default-profile.png"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 401,
                                          "message": "인증에 실패하였습니다."
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "사용자 또는 매장 정보 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "매장 정보를 찾을 수 없습니다."
                                        }
                                        """)))
        })
        ResponseEntity<?> getMyPage();

        @Operation(summary = "프로필 및 가게 정보 수정", description = """
                        로그인한 소상공인의 가게 정보를 수정합니다.
                        수정 가능한 정보는 가게 이름, 지도 링크, 위도, 경도입니다.
                        유효하지 않은 링크 입력 시 "링크가 유효하지 않습니다." 에러 메시지를 반환합니다.
                        Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "프로필 정보 수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "email": "owner@example.com",
                                          "storeName": "수정된 가게 이름",
                                          "mapUrl": "https://map.naver.com/p/updated",
                                          "latitude": 35.837777,
                                          "longitude": 128.753333,
                                          "profileImageUrl": "https://{bucket}.s3.ap-northeast-2.amazonaws.com/default/default-profile.png"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 링크", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "링크 오류", value = """
                                                        {
                                                          "status": 400,
                                                          "message": "링크가 유효하지 않습니다."
                                                        }
                                                        """),
                                        @ExampleObject(name = "필수값 누락", value = """
                                                        {
                                                          "status": 400,
                                                          "message": "가게 이름은 필수 입력입니다."
                                                        }
                                                        """)
                        })),
                        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 401,
                                          "message": "인증에 실패하였습니다."
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "사용자 또는 매장 정보 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "매장 정보를 찾을 수 없습니다."
                                        }
                                        """)))
        })
        ResponseEntity<?> updateProfile(
                        @Valid @RequestBody UserProfileUpdateReqDto dto);

        @Operation(summary = "프로필 이미지 수정", description = """
                        로그인한 사용자의 가게 프로필 이미지를 수정합니다.
                        프로필 이미지는 S3 Presigned URL 발급 API에서 domain을 PROFILE로 요청한 뒤,
                        응답으로 받은 presignedUrl에 프론트가 직접 PUT 업로드합니다.
                        업로드 성공 후 응답으로 받은 fileUrl을 요청 Body의 profileImageUrl에 담아 전달합니다.
                        profileImageUrl은 profile/{userId}/... 경로의 S3 URL이어야 합니다.
                        Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "프로필 이미지 수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "email": "owner@example.com",
                                          "storeName": "테스트가게",
                                          "mapUrl": "https://map.naver.com/p/example",
                                          "latitude": 35.836123,
                                          "longitude": 128.752345,
                                          "profileImageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/profile/3/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "잘못된 파일 URL", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "message": "허용되지 않은 파일 URL입니다."
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "404", description = "사용자 또는 매장 정보 없음")
        })
        ResponseEntity<?> updateProfileImage(
                        @Valid @RequestBody UserProfileImageUpdateReqDto dto);
}