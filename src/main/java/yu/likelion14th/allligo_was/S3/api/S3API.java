package yu.likelion14th.allligo_was.S3.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import yu.likelion14th.allligo_was.S3.dto.S3PresignedUrlReqDto;
import yu.likelion14th.allligo_was.S3.dto.S3PresignedUrlResDto;

@Tag(name = "S3 API", description = "S3 Presigned URL 발급 관련 API입니다.")
public interface S3API {

    @Operation(
            summary = "S3 Presigned URL 발급",
            description = """
                    로그인한 사용자가 이미지를 S3에 직접 업로드할 수 있도록 Presigned URL을 발급합니다.
                    
                    프론트는 업로드 도메인과 파일 Content-Type을 전달합니다.
                    백엔드는 로그인한 사용자 ID를 기준으로 S3 객체 경로를 생성합니다.
                    
                    응답값의 presignedUrl은 프론트가 S3에 PUT 업로드할 때 사용합니다.
                    응답값의 fileUrl은 업로드 성공 후 홍보/쿠폰/프로필/콘텐츠 API 요청 시 DB 저장용으로 전달합니다.
                    예를 들어 홍보 요청을 생성할 경우, 프론트는 S3 업로드 성공 후 해당 fileUrl을 imageUrls에 포함하여 홍보 생성 API로 전달해야 합니다.
                    
                    생성되는 S3 경로 형식:
                    - promotion/{userId}/{uuid}.{ext}
                    - coupon/{userId}/{uuid}.{ext}
                    - profile/{userId}/{uuid}.{ext}
                    - content/{userId}/{uuid}.{ext}
                    
                    지원 도메인:
                    - PROMOTION: 홍보 이미지
                    - COUPON: 쿠폰 이미지
                    - PROFILE: 프로필 이미지
                    - CONTENT: 생성된 콘텐츠 파일
                    
                    지원 Content-Type:
                    - image/png
                    - image/jpeg
                    - image/jpg
                    
                    주의:
                    - 이 API는 로그인 토큰이 필요합니다.
                    - S3 PUT 업로드 요청에는 JWT를 넣지 않습니다.
                    - S3 PUT 업로드 시 Content-Type은 Presigned URL 발급 요청 때 보낸 값과 동일해야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "presignedUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/1/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
                                              "fileUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/1/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청값",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "지원하지 않는 이미지 타입",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "png, jpg, jpeg 이미지만 업로드할 수 있습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "업로드 도메인 누락 또는 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "지원하지 않는 업로드 도메인입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "message": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<S3PresignedUrlResDto> createPresignedUrl(
            @Valid @RequestBody S3PresignedUrlReqDto request
    );
}