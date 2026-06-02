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

@Tag(name = "Content API", description = "콘텐츠 조회수 집계 및 리다이렉트 관련 API입니다.")
public interface ContentAPI {

    @Operation(
            summary = "콘텐츠 트래킹 및 리다이렉트",
            description = """
                    콘텐츠 클릭 시 호출되는 API입니다.
                    해당 콘텐츠의 클릭 로그 및 태그 클릭 로그를 저장하고, 소상공인의 가게 지도 URL(Location 헤더)로 리다이렉션합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "콘텐츠 트래킹 성공 후 소상공인 가게 지도 URL로 리다이렉트"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "콘텐츠, 소상공인 또는 가게 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "콘텐츠 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "message": "콘텐츠를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "사용자 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "message": "사용자를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "매장 정보 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "message": "매장 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<Void> trackAndRedirect(
            @Parameter(description = "트래킹할 콘텐츠 ID", example = "1")
            @PathVariable Long contentId
    );
}
