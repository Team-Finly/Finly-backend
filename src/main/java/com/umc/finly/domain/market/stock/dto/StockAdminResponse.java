package com.umc.finly.domain.market.stock.dto;

import com.umc.finly.domain.market.stock.enums.MarketType;

import java.util.List;

public class StockAdminResponse {
    public record StockSync(
            int totalCount,
            int kospiCount,
            int kosdaqCount,
            int deactivatedCount,
            List<StockSummary> updatedStocks,
            String description
    ) {
        public record StockSummary(String symbol, String name, String isin, MarketType marketType, Boolean isActive) {}
    }

    public record LogoUpdate(
            int targetCount,
            int updatedCount,
            int failedCount,
            List<LogoDetail> updatedLogos,
            String description
    ) {
        public record LogoDetail(String symbol, String name, MarketType marketType, String logoUrl) {}
    }

    public record TotalSync(
            StockSync syncDetail,
            LogoUpdate logoDetail
    ) {}
}
