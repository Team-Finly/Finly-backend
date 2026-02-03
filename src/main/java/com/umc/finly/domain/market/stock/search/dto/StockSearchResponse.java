package com.umc.finly.domain.market.stock.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StockSearchResponse {

    private String searchKeyword;
    private long totalCount;
    private List<Stock> stocks;
    private PageInfo pageInfo;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Stock {
        private long id;
        private String symbol;
        private String name;
        private String marketType;
        private String logoUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PageInfo {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalElements;
        private boolean isLast;
    }
}
