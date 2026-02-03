package com.umc.finly.domain.market.stock.search.converter;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.search.dto.StockSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockSearchConverter {

    // 검색어와 Page<Stock>를 종합하여 전체 검색 응답 DTO(StockSearchResponse)로 변환
    public StockSearchResponse toStockSearchResponse(String keyword, Page<Stock> stockPage) {
        List<StockSearchResponse.Stock> stocks = stockPage.getContent()
                .stream()
                .map(this::toStock)   // 여기서 반환 타입이 StockSearchResponse.Stock 여야 함
                .toList();

        StockSearchResponse.PageInfo pageInfo = StockSearchResponse.PageInfo.builder()
                .currentPage(stockPage.getNumber())
                .pageSize(stockPage.getSize())
                .totalElements(stockPage.getTotalElements())
                .totalPages(stockPage.getTotalPages())
                .isLast(stockPage.isLast())
                .build();

        return StockSearchResponse.builder()
                .searchKeyword(keyword)
                .totalCount(stockPage.getTotalElements())
                .stocks(stocks)
                .pageInfo(pageInfo)
                .build();
    }

    // 개별 Stock 엔티티 하나를 검색 결과용 DTO(StockSearchResponse.Stock)로 변환
    private StockSearchResponse.Stock toStock(Stock stock) {
        return StockSearchResponse.Stock.builder()
                .id(stock.getId())
                .symbol(stock.getSymbol())
                .name(stock.getName())
                .marketType(stock.getMarketType().name())
                .logoUrl(stock.getLogoUrl())
                .build();
    }
}