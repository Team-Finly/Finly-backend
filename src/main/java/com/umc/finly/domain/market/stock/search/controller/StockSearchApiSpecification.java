package com.umc.finly.domain.market.stock.search.controller;

import com.umc.finly.domain.market.stock.search.dto.StockSearchResDTO;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

@Tag(name = "StockSearch", description = "종목명 검색")
public interface StockSearchApiSpecification {
    @Operation(summary = "종목명 검색 API", description = "검색어를 종목명에 포함하는 종목을 찾아 반환합니다.")
    @Parameters({
            @Parameter(name = "keyword", description = "검색어", example = "삼성", required = true),
            @Parameter(name = "page", description = "페이지 번호(0부터 시작)", example = "0", required = false),
            @Parameter(name = "size", description = "한 페이지에 표시될 종목 개수", example = "20", required = false),
            @Parameter(name = "sort", description = "정렬 기준(필드,방향)", example = "name,asc", required = false)
    })
    public ApiResponse<StockSearchResDTO> searchStocks(String keyword, Pageable pageable);
}