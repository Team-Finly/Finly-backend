package com.umc.finly.domain.market.stock.search.controller;

import com.umc.finly.domain.market.stock.search.dto.StockSearchResDTO;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

@Tag(name = "StockSearch", description = "종목명 검색 API")
public interface StockSearchApiSpecification {
    @Operation(summary = "종목명 검색", description = """
    검색어를 종목명에 포함하는 종목을 찾아 반환합니다.
    - 종목 배열은 검색어가 앞에 위치한 종목(검색어%) → 검색어가 뒤에 위치한 종목(%검색어) → 검색어가 중간에 위치한 종목(%검색어%) 순으로 정렬됩니다.
    - 이름에 해당 검색어를 포함한 종목이 없을 경우 빈 배열이 반환됩니다.
    
    ※ 로고 이미지가 비어있는(logoUrl = null) 종목도 존재합니다.
    """)
    @Parameters({
            @Parameter(name = "keyword", description = "검색어", example = "삼성", required = true),
            @Parameter(name = "page", description = "페이지 번호(0부터 시작)", example = "0", required = false),
            @Parameter(name = "size", description = "한 페이지에 표시될 종목 개수", example = "20", required = false),
            @Parameter(name = "sort", description = "정렬 기준(필드,방향)", example = "name,asc", required = false)
    })
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "잘못된 요청", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "필수 파라미터 keyword 값 누락",
                                    summary = "검색어 누락",
                                    value = "{ \"isSuccess\": false, \"code\": \"STOCK_SEARCH400\", \"message\": \"검색어가 비어 있습니다.\" }"
                            )
                    })
            )
    })
    public ApiResponse<StockSearchResDTO> searchStocks(String keyword, Pageable pageable);
}