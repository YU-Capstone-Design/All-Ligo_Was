package yu.likelion14th.allligo_was.domains.dashboard.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Dashboard API", description = "대시보드 통계 관련 API입니다.")
public interface DashboardAPI {

    @Operation(summary = "대시보드 통계 조회", description = """
            로그인한 소상공인의 대시보드 통계 데이터를 조회합니다.

            이 API는 대시보드 화면에 필요한 통계 카드 4개를 한 번에 반환합니다.

            조회 항목:
            1. 시간대별 클릭 수
            2. 요일별 클릭 수
            3. 태그별 클릭 비율
            4. 콘텐츠 유형별 클릭 비율

            기간 정책:
            - startDate와 endDate를 전달하지 않으면 이번 달 기준으로 조회합니다.
            - startDate는 조회 시작일입니다.
            - endDate는 조회 종료일입니다.
            - endDate는 해당 날짜 전체를 포함합니다.
            - 내부적으로는 startDate 00:00:00 이상, endDate + 1일 00:00:00 미만 조건으로 조회합니다.

            시간대별 클릭 수 정책:
            - 0시부터 23시까지 총 24개 데이터를 반환합니다.
            - 클릭이 없는 시간대는 clickCount 0으로 반환합니다.
            - averageClickCount는 24시간 클릭 수 평균을 반올림한 값입니다.
            - maxClickCount와 maxHour는 최고 클릭 수와 해당 시간입니다.
            - minClickCount와 minHour는 최저 클릭 수와 해당 시간입니다.

            요일별 클릭 수 정책:
            - 월요일부터 일요일까지 총 7개 데이터를 반환합니다.
            - 클릭이 없는 요일은 clickCount 0으로 반환합니다.
            - rank는 클릭 수 기준 공동 순위입니다.
            - 클릭 수가 같은 요일은 같은 rank를 가집니다.
            - 프론트는 rank 1을 가장 진한 막대, rank 2를 두 번째로 진한 막대, rank 3 이상을 기본 막대로 표시하면 됩니다.

            태그별 클릭 비율 정책:
            - 클릭 수 상위 3개 태그만 반환합니다.
            - 비율은 전체 태그 기준이 아니라 상위 3개 태그 클릭 수 합계를 100%로 보고 계산합니다.
            - 태그 클릭 데이터가 없으면 빈 배열을 반환합니다.

            콘텐츠 유형별 클릭 비율 정책:
            - POST와 VIDEO 기준으로 반환합니다.
            - POST label은 게시글입니다.
            - VIDEO label은 인스타그램입니다.
            - 클릭 데이터가 없어도 POST, VIDEO 두 항목은 항상 반환합니다.
            - 클릭 데이터가 없으면 clickCount와 ratio는 0입니다.

            Swagger 상단의 Authorize 버튼에 Bearer 토큰을 입력한 후 요청해야 합니다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대시보드 통계 조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "totalClicks": 457,
                      "hourlyClickStatistics": {
                        "averageClickCount": 38,
                        "maxClickCount": 114,
                        "maxHour": 20,
                        "minClickCount": 12,
                        "minHour": 7,
                        "hourlyClicks": [
                          {
                            "hour": 0,
                            "clickCount": 30
                          },
                          {
                            "hour": 1,
                            "clickCount": 24
                          }
                        ]
                      },
                      "dayOfWeekClickStatistics": {
                        "maxClickCount": 164,
                        "secondClickCount": 120,
                        "dayOfWeekClicks": [
                          {
                            "dayOfWeek": "MON",
                            "label": "월",
                            "clickCount": 72,
                            "rank": 3
                          },
                          {
                            "dayOfWeek": "TUE",
                            "label": "화",
                            "clickCount": 45,
                            "rank": 4
                          },
                          {
                            "dayOfWeek": "WED",
                            "label": "수",
                            "clickCount": 164,
                            "rank": 1
                          },
                          {
                            "dayOfWeek": "THU",
                            "label": "목",
                            "clickCount": 120,
                            "rank": 2
                          },
                          {
                            "dayOfWeek": "FRI",
                            "label": "금",
                            "clickCount": 68,
                            "rank": 3
                          },
                          {
                            "dayOfWeek": "SAT",
                            "label": "토",
                            "clickCount": 92,
                            "rank": 3
                          },
                          {
                            "dayOfWeek": "SUN",
                            "label": "일",
                            "clickCount": 110,
                            "rank": 3
                          }
                        ]
                      },
                      "tagClickStatistics": {
                        "topTagClickRatios": [
                          {
                            "tagName": "키워드1",
                            "clickCount": 65,
                            "ratio": 47
                          },
                          {
                            "tagName": "키워드2",
                            "clickCount": 46,
                            "ratio": 33
                          },
                          {
                            "tagName": "키워드3",
                            "clickCount": 28,
                            "ratio": 20
                          }
                        ]
                      },
                      "contentTypeClickStatistics": {
                        "contentTypeRatios": [
                          {
                            "contentType": "POST",
                            "label": "게시글",
                            "clickCount": 64,
                            "ratio": 64
                          },
                          {
                            "contentType": "VIDEO",
                            "label": "인스타그램",
                            "clickCount": 36,
                            "ratio": 36
                          }
                        ]
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "message": "인증에 실패하였습니다."
                    }
                    """)))
    })
    ResponseEntity<?> getDashboardStatistics(
            @Parameter(description = "조회 시작일입니다. 전달하지 않으면 이번 달 1일 기준으로 조회합니다.", example = "2026-06-01") @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "조회 종료일입니다. 전달하지 않으면 이번 달 마지막 날 기준으로 조회합니다.", example = "2026-06-30") @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}