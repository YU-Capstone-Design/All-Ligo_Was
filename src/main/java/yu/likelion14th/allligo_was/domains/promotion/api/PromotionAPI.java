package yu.likelion14th.allligo_was.domains.promotion.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionCreateReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.request.PromotionUpdateReqDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionDetailResDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionListResDto;
import yu.likelion14th.allligo_was.domains.promotion.dto.response.PromotionScheduleQueueResDto;

import java.util.List;

@Tag(name = "Promotion API", description = "홍보 콘텐츠 생성 요청 및 홍보 요청 관리 API입니다.")
public interface PromotionAPI {

    @Operation(
            summary = "홍보 콘텐츠 생성 요청",
            description = """
                    로그인한 소상공인이 홍보 콘텐츠 생성을 요청합니다.
                    홍보 제목, 콘텐츠 타입, 프롬프트, 날씨 사용 여부, 분위기 태그, 마감일, 이미지 URL, 해시태그, 스케줄 정보를 함께 등록합니다.

                    contentType은 BLOG 또는 VIDEO만 가능합니다.
                    mode는 따뜻함, 차분함, 밝음 중 하나만 가능합니다.
                    imageUrls는 1장 이상 5장 이하입니다.
                    tags는 10개 미만이며, 각 태그는 7자 이하입니다.
                    schedules는 1개 이상 등록해야 하며, publishTime은 현재 시간보다 1시간 이후여야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "홍보 요청 생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "promotionId": 1,
                      "contentType": "BLOG",
                      "promotionTitle": "비오는날커피홍보",
                      "prompt": "비 오는 날 따뜻한 라떼 할인 이벤트를 홍보하는 블로그 글을 만들어줘",
                      "weatherEnabled": true,
                      "mode": "따뜻함",
                      "deadline": "2026-06-30T23:59:00",
                      "createdAt": "2026-06-02T20:40:00",
                      "updatedAt": "2026-06-02T20:40:00",
                      "imageUrls": [
                        "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg",
                        "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image2.jpg"
                      ],
                      "tags": [
                        "라떼",
                        "할인",
                        "비오는날"
                      ],
                      "schedules": [
                        {
                          "scheduleId": 1,
                          "dayOfWeek": "FRIDAY",
                          "publishTime": "2026-06-07T18:00:00"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "콘텐츠 타입 오류", value = """
                            {
                              "status": 400,
                              "message": "콘텐츠 타입은 BLOG 또는 VIDEO만 가능합니다."
                            }
                            """),
                    @ExampleObject(name = "분위기 태그 오류", value = """
                            {
                              "status": 400,
                              "message": "분위기 태그는 따뜻함, 차분함, 밝음 중 하나만 선택할 수 있습니다."
                            }
                            """),
                    @ExampleObject(name = "이미지 개수 오류", value = """
                            {
                              "status": 400,
                              "message": "이미지는 1장 이상 5장 이하로 등록해야 합니다."
                            }
                            """),
                    @ExampleObject(name = "배포 시간 오류", value = """
                            {
                              "status": 400,
                              "message": "배포 시간은 현재 시간보다 1시간 이후여야 합니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "message": "사용자를 찾을 수 없습니다."
                    }
                    """)))
    })
    ResponseEntity<PromotionDetailResDto> createPromotion(
            @Valid @RequestBody PromotionCreateReqDto request
    );

    @Operation(
            summary = "내 홍보 요청 목록 조회",
            description = """
                    로그인한 소상공인이 등록한 홍보 요청 목록을 조회합니다.
                    홍보 목록 카드에 필요한 홍보 ID, 홍보 제목, 첫 번째 이미지 URL, 등록일, 콘텐츠 타입만 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "홍보 요청 목록 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "promotionId": 2,
                        "promotionTitle": "딸기라떼숏츠홍보",
                        "thumbnailImageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg",
                        "createdAt": "2026-06-02T20:50:00",
                        "contentType": "VIDEO"
                      },
                      {
                        "promotionId": 1,
                        "promotionTitle": "비오는날커피홍보",
                        "thumbnailImageUrl": "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg",
                        "createdAt": "2026-06-02T20:40:00",
                        "contentType": "BLOG"
                      }
                    ]
                    """)))
    })
    ResponseEntity<List<PromotionListResDto>> getMyPromotions();

    @Operation(
            summary = "홍보 요청 상세 조회",
            description = """
                    특정 홍보 요청의 상세 정보를 조회합니다.
                    홍보 기본 정보, 이미지 URL 목록, 태그 목록, 스케줄 목록을 함께 반환합니다.
                    본인이 등록한 홍보 요청만 조회할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "홍보 요청 상세 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "promotionId": 1,
                      "contentType": "BLOG",
                      "promotionTitle": "비오는날커피홍보",
                      "prompt": "비 오는 날 따뜻한 라떼 할인 이벤트를 홍보하는 블로그 글을 만들어줘",
                      "weatherEnabled": true,
                      "mode": "따뜻함",
                      "deadline": "2026-06-30T23:59:00",
                      "createdAt": "2026-06-02T20:40:00",
                      "updatedAt": "2026-06-02T20:40:00",
                      "imageUrls": [
                        "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg",
                        "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image2.jpg"
                      ],
                      "tags": [
                        "라떼",
                        "할인",
                        "비오는날"
                      ],
                      "schedules": [
                        {
                          "scheduleId": 1,
                          "dayOfWeek": "FRIDAY",
                          "publishTime": "2026-06-07T18:00:00"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "홍보 요청 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "message": "해당 홍보 요청을 찾을 수 없습니다."
                    }
                    """)))
    })
    ResponseEntity<PromotionDetailResDto> getPromotionDetail(
            @Parameter(description = "조회할 홍보 요청 ID", example = "1")
            @PathVariable Long promotionId
    );

    @Operation(
            summary = "홍보 요청 전체 수정",
            description = """
                    특정 홍보 요청의 전체 정보를 수정합니다.
                    기존값을 포함한 전체 요청 데이터를 전달해야 합니다.

                    수정 대상은 홍보 제목, 콘텐츠 타입, 프롬프트, 날씨 사용 여부, 분위기 태그, 마감일, 이미지 URL 목록, 태그 목록, 스케줄 목록입니다.
                    이미지, 태그, 스케줄은 기존 데이터를 전체 삭제한 뒤 요청값으로 다시 저장합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "홍보 요청 수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "promotionId": 1,
                      "contentType": "BLOG",
                      "promotionTitle": "수정된커피홍보",
                      "prompt": "비 오는 날 방문 고객을 대상으로 따뜻한 라떼 할인 이벤트를 홍보하는 글을 만들어줘",
                      "weatherEnabled": true,
                      "mode": "차분함",
                      "deadline": "2026-07-10T23:59:00",
                      "createdAt": "2026-06-02T20:40:00",
                      "updatedAt": "2026-06-02T21:10:00",
                      "imageUrls": [
                        "https://all-ligo-images.s3.ap-northeast-2.amazonaws.com/promotion/test/promotion_image1.jpg"
                      ],
                      "tags": [
                        "라떼",
                        "할인",
                        "이벤트"
                      ],
                      "schedules": [
                        {
                          "scheduleId": 3,
                          "dayOfWeek": "SATURDAY",
                          "publishTime": "2026-06-08T13:00:00"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 수정 요청", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "홍보 제목 오류", value = """
                            {
                              "status": 400,
                              "message": "홍보 제목은 3자 이상, 20자 이하로 입력해주세요."
                            }
                            """),
                    @ExampleObject(name = "스케줄 오류", value = """
                            {
                              "status": 400,
                              "message": "스케줄을 1개 이상 등록해야 합니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", description = "홍보 요청 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "message": "해당 홍보 요청을 찾을 수 없습니다."
                    }
                    """)))
    })
    ResponseEntity<PromotionDetailResDto> updatePromotion(
            @Parameter(description = "수정할 홍보 요청 ID", example = "1")
            @PathVariable Long promotionId,

            @Valid @RequestBody PromotionUpdateReqDto request
    );

    @Operation(
            summary = "홍보 요청 삭제",
            description = """
                    특정 홍보 요청을 삭제합니다.
                    홍보 요청에 연결된 content, promotion_execution, promotion_schedule, promotion_image, promotion_tag 데이터도 함께 삭제합니다.
                    본인이 등록한 홍보 요청만 삭제할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "홍보 요청 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "홍보 요청 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "message": "해당 홍보 요청을 찾을 수 없습니다."
                    }
                    """)))
    })
    ResponseEntity<Void> deletePromotion(
            @Parameter(description = "삭제할 홍보 요청 ID", example = "1")
            @PathVariable Long promotionId
    );

    @Operation(
            summary = "스케줄링 대기열 조회",
            description = """
                    로그인한 소상공인의 24시간 이내 콘텐츠 스케줄링 대기열을 조회합니다.
                    
                    PromotionExecution 상태가 PENDING이면 대기중,
                    PROCESSING이면 생성중,
                    FAILED이면 실패로 응답합니다.
                    
                    PromotionExecution 상태가 SUCCESS인 경우에는 연결된 Content 상태를 확인합니다.
                    Content 상태가 GENERATED인 경우에만 생성 완료 항목으로 응답합니다.
                    
                    Content 상태가 PUBLISHED 또는 CANCELLED인 콘텐츠는 대기열에서 제외됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스케줄링 대기열 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "executionId": 1,
                                                "promotionId": 3,
                                                "contentId": null,
                                                "promotionTitle": "비오는날커피홍보",
                                                "contentType": "VIDEO",
                                                "contentTypeLabel": "영상",
                                                "status": "PENDING",
                                                "statusLabel": "대기중",
                                                "executedAt": "2026-06-02T23:30:00",
                                                "createdAt": null,
                                                "expiresAt": null,
                                                "clickable": false
                                              },
                                              {
                                                "executionId": 2,
                                                "promotionId": 3,
                                                "contentId": null,
                                                "promotionTitle": "비오는날커피홍보",
                                                "contentType": "VIDEO",
                                                "contentTypeLabel": "영상",
                                                "status": "PROCESSING",
                                                "statusLabel": "생성중",
                                                "executedAt": "2026-06-03T01:30:00",
                                                "createdAt": null,
                                                "expiresAt": null,
                                                "clickable": false
                                              },
                                              {
                                                "executionId": 3,
                                                "promotionId": 3,
                                                "contentId": null,
                                                "promotionTitle": "비오는날커피홍보",
                                                "contentType": "VIDEO",
                                                "contentTypeLabel": "영상",
                                                "status": "FAILED",
                                                "statusLabel": "실패",
                                                "executedAt": "2026-06-03T03:30:00",
                                                "createdAt": null,
                                                "expiresAt": null,
                                                "clickable": false
                                              },
                                              {
                                                "executionId": 4,
                                                "promotionId": 3,
                                                "contentId": 5,
                                                "promotionTitle": "비오는날커피홍보",
                                                "contentType": "VIDEO",
                                                "contentTypeLabel": "영상",
                                                "status": "GENERATED",
                                                "statusLabel": "생성 완료",
                                                "executedAt": "2026-06-03T05:30:00",
                                                "createdAt": "2026-06-02T21:30:00",
                                                "expiresAt": "2026-06-03T21:30:00",
                                                "clickable": true
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    ResponseEntity<List<PromotionScheduleQueueResDto>> getScheduleQueue();
}