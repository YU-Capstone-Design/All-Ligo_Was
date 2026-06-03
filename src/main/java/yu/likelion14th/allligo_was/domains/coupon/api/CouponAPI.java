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
import org.springframework.web.bind.annotation.PathVariable;
import yu.likelion14th.allligo_was.domains.coupon.dto.request.CouponUpdateReqDto;

@Tag(name = "Coupon API", description = "쿠폰 등록 및 관리 관련 API입니다.")
public interface CouponAPI {

  @Operation(summary = "쿠폰 등록", description = """
      로그인한 소상공인이 쿠폰을 등록합니다.
      쿠폰 이미지는 S3 Presigned URL 발급 API에서 domain을 COUPON으로 요청한 뒤,
      응답으로 받은 presignedUrl에 프론트가 직접 PUT 업로드합니다.
      업로드 성공 후 응답으로 받은 fileUrl을 요청 Body의 imageUrl에 담아 전달합니다.
      imageUrl은 coupon/{userId}/... 경로의 S3 URL이어야 합니다.
      Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
      """)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "쿠폰 등록 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "couponId": 1,
            "imageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/3/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png",
            "menuName": "아메리카노",
            "discountNum": 1000,
            "discountType": "AMOUNT",
            "message": "쿠폰이 등록되었습니다."
          }
          """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "이미지 URL 오류", value = """
              {
                "status": 400,
                "message": "허용되지 않은 파일 URL입니다."
              }
              """),
          @ExampleObject(name = "필수값 누락", value = """
              {
                "status": 400,
                "message": "메뉴명은 필수 입력입니다."
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
  ResponseEntity<?> createCoupon(
      @Valid @RequestBody CouponCreateReqDto dto);

  @Operation(summary = "내 쿠폰 목록 조회", description = """
      로그인한 소상공인이 등록한 쿠폰 목록을 조회합니다.
      Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
      """)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "내 쿠폰 목록 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          [
            {
              "couponId": 1,
              "imageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/3/2e208521-86c3-4c9f-8c84-7cd4c05ecdc4.png",
              "menuName": "아메리카노",
              "discountNum": 1000,
              "discountType": "AMOUNT"
            },
            {
              "couponId": 2,
              "imageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/3/7b83c02a-5f32-49f4-8c12-2a4f7fb8431d.png",
              "menuName": "카페라떼",
              "discountNum": 10,
              "discountType": "RATE"
            }
          ]
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
  ResponseEntity<?> getMyCoupons();

  @Operation(summary = "쿠폰 수정", description = """
      로그인한 소상공인이 본인이 등록한 쿠폰 정보를 수정합니다.
      쿠폰 이미지는 S3 Presigned URL 발급 API에서 domain을 COUPON으로 요청한 뒤,
      응답으로 받은 presignedUrl에 프론트가 직접 PUT 업로드합니다.
      업로드 성공 후 응답으로 받은 fileUrl을 요청 Body의 imageUrl에 담아 전달합니다.
      imageUrl은 coupon/{userId}/... 경로의 S3 URL이어야 합니다.
      Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
      """)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "쿠폰 수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "couponId": 1,
            "imageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/3/9f1e8f3a-0e3b-4c4a-9b11-6a9f5a7b8c31.png",
            "menuName": "카페라떼",
            "discountNum": 10,
            "discountType": "RATE",
            "message": "쿠폰이 수정되었습니다."
          }
          """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 400,
            "message": "허용되지 않은 파일 URL입니다."
          }
          """))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 401,
            "message": "인증에 실패하였습니다."
          }
          """))),
      @ApiResponse(responseCode = "403", description = "본인 쿠폰이 아님", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 403,
            "message": "해당 쿠폰에 접근할 수 없습니다."
          }
          """))),
      @ApiResponse(responseCode = "404", description = "쿠폰 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 404,
            "message": "쿠폰을 찾을 수 없습니다."
          }
          """)))
  })
  ResponseEntity<?> updateCoupon(
      @PathVariable("couponId") Long couponId,
      @Valid @RequestBody CouponUpdateReqDto dto);

  @Operation(summary = "쿠폰 삭제", description = """
      로그인한 소상공인이 본인이 등록한 쿠폰을 삭제합니다.
      Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
      """)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "쿠폰 삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "couponId": 1,
            "imageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/coupon/3/9f1e8f3a-0e3b-4c4a-9b11-6a9f5a7b8c31.png",
            "menuName": "카페라떼",
            "discountNum": 10,
            "discountType": "RATE",
            "message": "쿠폰이 삭제되었습니다."
          }
          """))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 401,
            "message": "인증에 실패하였습니다."
          }
          """))),
      @ApiResponse(responseCode = "403", description = "본인 쿠폰이 아님", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 403,
            "message": "해당 쿠폰에 접근할 수 없습니다."
          }
          """))),
      @ApiResponse(responseCode = "404", description = "쿠폰 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 404,
            "message": "쿠폰을 찾을 수 없습니다."
          }
          """)))
  })
  ResponseEntity<?> deleteCoupon(
      @PathVariable("couponId") Long couponId);

  @Operation(summary = "지역 기반 쿠폰 조회", description = """
      지정된 행정구역에 위치한 매장 정보와 해당 매장들이 보유한 쿠폰 리스트를 조회합니다.
      비로그인 상태에서 접근이 가능한 전용 API입니다.
      """)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "지역별 쿠폰 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          [
            {
              "storeId": 1,
              "storeName": "LIGO 매장",
              "region": "대구",
              "latitude": 35.8711,
              "longitude": 128.6014,
              "mapUrl": "https://map.naver.com/...",
              "profileImageUrl": "https://...",
              "coupons": [
                {
                  "couponId": 1,
                  "imageUrl": "https://...",
                  "menuName": "아메리카노",
                  "discountNum": 1000,
                  "discountType": "AMOUNT"
                }
              ]
            }
          ]
          """))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "status": 400,
            "message": "올바르지 않은 행정구역입니다."
          }
          """)))
  })
  ResponseEntity<?> getCouponsByRegion(String region);

  @Operation(summary = "현위치 기반 반경 3km 쿠폰 조회", description = """
      사용자의 현재 위경도 좌표를 기준으로 3km 이내에 위치한 매장과 해당 매장들이 보유한 쿠폰 리스트를 가까운 순서대로 조회합니다.
      비로그인 상태에서 접근이 가능한 전용 API입니다.
      """)
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "반경 내 쿠폰 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          [
            {
              "storeId": 1,
              "storeName": "LIGO 매장",
              "region": "대구",
              "latitude": 35.8711,
              "longitude": 128.6014,
              "mapUrl": "https://map.naver.com/...",
              "profileImageUrl": "https://...",
              "coupons": [
                {
                  "couponId": 1,
                  "imageUrl": "https://...",
                  "menuName": "아메리카노",
                  "discountNum": 1000,
                  "discountType": "AMOUNT"
                }
              ]
            }
          ]
          """)))
  })
  ResponseEntity<?> getNearbyCoupons(Double latitude, Double longitude);
}