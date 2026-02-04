package com.umc.finly.domain.analysis.stock.infra;

public interface StockPriceProvider {
    Integer getCurrentPrice(String stockCode);
}
