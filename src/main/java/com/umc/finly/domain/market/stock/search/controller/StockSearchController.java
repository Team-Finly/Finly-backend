package com.umc.finly.domain.market.stock.search.controller;

import com.umc.finly.domain.market.stock.search.dto.StockSearchResDTO;
import com.umc.finly.domain.market.stock.search.service.StockSearchService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockSearchController implements StockSearchApiSpecification {

    private final StockSearchService stockSearchService;

    // 종목명 검색 API
    @GetMapping("/search")
    public ApiResponse<StockSearchResDTO> searchStocks(
            @RequestParam(name = "keyword") String keyword,
            @PageableDefault(page = 0, size = 20, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        StockSearchResDTO response = stockSearchService.searchStocks(keyword, pageable);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }
}
