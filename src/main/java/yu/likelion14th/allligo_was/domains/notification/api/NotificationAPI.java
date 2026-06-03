package yu.likelion14th.allligo_was.domains.notification.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import yu.likelion14th.allligo_was.domains.notification.dto.response.NotificationResDto;

import java.util.List;

@Tag(name = "Notification API", description = "웹 알림 조회 및 읽음 처리 API입니다.")
public interface NotificationAPI {

    @Operation(
            summary = "알림 목록 조회",
            description = """
                    로그인한 사용자의 알림 목록을 최신순으로 조회합니다.
                    
                    최대 20개의 알림을 반환합니다.
                    알림 목록을 조회하는 것만으로는 읽음 처리되지 않습니다.
                    
                    알림 제목은 홍보 요청 제목을 기반으로 생성됩니다.
                    예: {홍보 요청 제목} 콘텐츠 생성 완료
                    
                    사용자가 알림을 클릭하면 별도의 읽음 처리 API를 호출한 뒤,
                    응답에 포함된 contentId를 사용하여 콘텐츠 미리보기 페이지로 이동하면 됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "notificationId": 1,
                                                "contentId": 10,
                                                "notificationTitle": "여름 신메뉴 홍보 콘텐츠 생성 완료",
                                                "message": "생성된 콘텐츠를 확인해보세요!",
                                                "isRead": false,
                                                "createdAt": "2026-06-03T15:30:00"
                                              },
                                              {
                                                "notificationId": 2,
                                                "contentId": 11,
                                                "notificationTitle": "아메리카노 할인 홍보 콘텐츠 생성 완료",
                                                "message": "생성된 콘텐츠를 확인해보세요!",
                                                "isRead": true,
                                                "createdAt": "2026-06-03T13:10:00"
                                              }
                                            ]
                                            """
                            )
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
    ResponseEntity<List<NotificationResDto>> getMyNotifications();

    @Operation(
            summary = "알림 읽음 처리",
            description = """
                    특정 알림을 읽음 처리합니다.
                    
                    현재 로그인한 사용자의 알림만 읽음 처리할 수 있습니다.
                    일반적으로 프론트에서 알림 클릭 후 미리보기 페이지로 이동하기 전에 호출합니다.
                    
                    알림 목록 조회만으로는 읽음 처리되지 않습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "알림 읽음 처리 성공"
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "알림을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "해당 알림을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId
    );
}