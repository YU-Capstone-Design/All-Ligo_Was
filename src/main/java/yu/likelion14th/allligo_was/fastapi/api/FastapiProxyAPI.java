package yu.likelion14th.allligo_was.fastapi.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiContentResponseDto;
import yu.likelion14th.allligo_was.fastapi.dto.FastapiGenerateReqDto;

import java.util.List;

@Tag(name = "FastAPI Proxy API (Internal)", description = "스프링 WAS와 FastAPI AI 에이전트 간 통신을 위한 프록시 API입니다. **프론트엔드에서는 호출하거나 신경 쓰실 필요가 없습니다.**")
public interface FastapiProxyAPI {

    @Operation(
        summary = "콘텐츠 생성 요청 (FastAPI 프록시)", 
        description = "이미지 리스트와 요청 정보를 받아 S3에 이미지를 업로드하고, FastAPI AI 에이전트 서버에 콘텐츠 생성을 요청합니다. **[주의] 프론트엔드 연동용 API가 아닙니다.**"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "요청 성공 (FastAPI 작업 ID 반환)", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FastapiContentResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 (이미지 개수 초과(>5) 또는 비어있는 파일 전송 시)"
        )
    })
    ResponseEntity<FastapiContentResponseDto> generateContent(
            @ModelAttribute FastapiGenerateReqDto reqDto,
            @Parameter(description = "콘텐츠 생성에 사용할 이미지 목록 (1~5장)") @RequestParam("images") List<MultipartFile> images
    );
}
