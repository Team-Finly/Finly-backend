package com.umc.finly.domain.market.stock.info.dto;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.enums.MarketType;
import lombok.Getter;

/**
 * Stock 엔티티를 API 응답으로 보낼 때 사용하는 DTO.
 */
@Getter
public class StockInfoResponse {
    private String symbol;
    private String name;
    private MarketType marketType;
    private String isin;
    private Boolean isActive;
    private String logoUrl;

    public static StockInfoResponse from(Stock stock) {
        StockInfoResponse dto = new StockInfoResponse();
        dto.symbol = stock.getSymbol();
        dto.name = stock.getName();
        dto.marketType = stock.getMarketType();
        dto.isin = stock.getIsin();
        dto.isActive = stock.getIsActive();
        dto.logoUrl = stock.getLogoUrl();
        return dto;
    }
}
