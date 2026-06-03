package yu.likelion14th.allligo_was.domains.content.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import yu.likelion14th.allligo_was.domains.content.dto.response.ContentCancelResDto;
import yu.likelion14th.allligo_was.domains.content.dto.response.ContentPreviewResDto;

@Tag(name = "Content API", description = "콘텐츠 조회수 집계 및 리다이렉트 관련 API입니다.")
public interface ContentAPI {

        @Operation(summary = "콘텐츠 트래킹 및 리다이렉트", description = """
                        콘텐츠 클릭 시 호출되는 API입니다.
                        해당 콘텐츠의 클릭 로그 및 태그 클릭 로그를 저장하고, 소상공인의 가게 지도 URL(Location 헤더)로 리다이렉션합니다.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "302", description = "콘텐츠 트래킹 성공 후 소상공인 가게 지도 URL로 리다이렉트"),
                        @ApiResponse(responseCode = "404", description = "콘텐츠, 소상공인 또는 가게 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "콘텐츠 없음", value = """
                                                        {
                                                          "status": 404,
                                                          "message": "콘텐츠를 찾을 수 없습니다."
                                                        }
                                                        """),
                                        @ExampleObject(name = "사용자 없음", value = """
                                                        {
                                                          "status": 404,
                                                          "message": "사용자를 찾을 수 없습니다."
                                                        }
                                                        """),
                                        @ExampleObject(name = "매장 정보 없음", value = """
                                                        {
                                                          "status": 404,
                                                          "message": "매장 정보를 찾을 수 없습니다."
                                                        }
                                                        """)
                        }))
        })
        ResponseEntity<Void> trackAndRedirect(
                        @Parameter(description = "트래킹할 콘텐츠 ID", example = "1") @PathVariable("contentId") Long contentId);

        @Operation(summary = "생성 완료 콘텐츠 미리보기", description = """
                        생성 완료된 콘텐츠의 미리보기 정보를 조회합니다.

                        콘텐츠 상태가 GENERATED인 경우에만 조회할 수 있습니다.
                        BLOG 또는 POST 타입은 본문과 이미지 정보를 포함하고,
                        VIDEO 타입은 캡션과 영상 URL 정보를 포함합니다.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "생성 완료 콘텐츠 미리보기 조회 성공"),
                        @ApiResponse(responseCode = "400", description = "생성 완료 상태가 아닌 콘텐츠를 조회한 경우", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "미리보기 불가 상태", value = """
                                        {
                                          "status": 400,
                                          "message": "생성 완료된 콘텐츠만 미리보기할 수 있습니다."
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "콘텐츠를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "콘텐츠를 찾을 수 없습니다."
                                        }
                                        """)))
        })
        ResponseEntity<ContentPreviewResDto> getContentPreview(
                        @Parameter(description = "미리보기할 콘텐츠 ID", example = "1") @PathVariable("contentId") Long contentId);

        @Operation(summary = "생성 콘텐츠 배포 중단", description = """
                        생성 완료된 콘텐츠의 배포를 중단합니다.

                        이 API는 스케줄링 자체를 삭제하지 않습니다.
                        이미 생성된 특정 Content의 상태만 CANCELLED로 변경합니다.
                        Promotion, PromotionSchedule, PromotionExecution 데이터는 유지됩니다.

                        콘텐츠 상태가 GENERATED인 경우에만 배포 중단할 수 있습니다.
                        이미 CANCELLED 상태인 콘텐츠, 이미 PUBLISHED 상태인 콘텐츠는 각각 별도 에러 메시지를 반환합니다.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "콘텐츠 배포 중단 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "contentId": 1,
                                          "status": "CANCELLED",
                                          "message": "콘텐츠 배포가 중단되었습니다."
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "배포 중단할 수 없는 콘텐츠 상태", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(name = "생성 완료 상태 아님", value = """
                                                        {
                                                          "status": 400,
                                                          "message": "생성 완료된 콘텐츠만 배포 중단할 수 있습니다."
                                                        }
                                                        """),
                                        @ExampleObject(name = "이미 취소됨", value = """
                                                        {
                                                          "status": 400,
                                                          "message": "이미 배포 중단된 콘텐츠입니다."
                                                        }
                                                        """),
                                        @ExampleObject(name = "이미 업로드됨", value = """
                                                        {
                                                          "status": 400,
                                                          "message": "이미 업로드된 콘텐츠는 배포 중단할 수 없습니다."
                                                        }
                                                        """)
                        })),
                        @ApiResponse(responseCode = "404", description = "콘텐츠를 찾을 수 없음", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "콘텐츠를 찾을 수 없습니다."
                                        }
                                        """)))
        })
        ResponseEntity<ContentCancelResDto> cancelContent(
                        @Parameter(description = "배포 중단할 콘텐츠 ID", example = "1") @PathVariable("contentId") Long contentId);
}
