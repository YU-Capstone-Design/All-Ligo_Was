package yu.likelion14th.allligo_was.domains.coupon.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponCreateReqDto;

@Tag(name = "Coupon API", description = "쿠폰 등록 및 관리 관련 API입니다.")
public interface CouponAPI {

    @Operation(
            summary = "쿠폰 등록",
            description = """
                    로그인한 소상공인이 쿠폰을 등록합니다.
                    쿠폰 이미지는 프론트가 S3에 직접 업로드한 뒤, 업로드 완료된 imageUrl을 요청 Body에 담아 전달합니다.
                    Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "couponId": 1,
                                              "imageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/example.png",
                                              "menuName": "아메리카노",
                                              "discountNum": 1000,
                                              "discountType": "AMOUNT",
                                              "message": "쿠폰이 등록되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "이미지 URL 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "쿠폰 이미지 URL이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "필수값 누락",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "메뉴명은 필수 입력입니다."
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
    ResponseEntity<?> createCoupon(
            @Valid @RequestBody CouponCreateReqDto dto
    );
}