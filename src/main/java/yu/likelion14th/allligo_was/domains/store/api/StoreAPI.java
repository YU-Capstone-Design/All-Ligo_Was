package yu.likelion14th.allligo_was.domains.store.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import yu.likelion14th.allligo_was.domains.store.dto.response.PublicStoreResDto;

import java.util.List;

@Tag(name = "Store", description = "매장 조회 API")
public interface StoreAPI {

    @Operation(summary = "비로그인 전체 매장 목록 조회", description = """
            DB에 등록된 모든 매장을 조회합니다.
            로그인 없이 호출할 수 있으며,
            매장별 쿠폰 개수와 발행된 콘텐츠 개수를 함께 반환합니다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 매장 목록 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PublicStoreResDto.class)), examples = @ExampleObject(name = "매장 목록 조회 예시", value = """
                    [
                      {
                        "storeId": 5,
                        "storeName": "카페101",
                        "latitude": 35.8379,
                        "longitude": 128.7542,
                        "address": "경북 경산시 …",
                        "profileImageUrl": "https://example.com/store.jpg",
                        "couponCount": 3,
                        "contentCount": 2
                      }
                    ]
                    """)))
    })
    ResponseEntity<List<PublicStoreResDto>> getPublicStores();
}
