package com.umc.finly.domain.analysis.infra;

public interface StockPriceProvider {
    Integer getCurrentPrice(String stockCode);
}
