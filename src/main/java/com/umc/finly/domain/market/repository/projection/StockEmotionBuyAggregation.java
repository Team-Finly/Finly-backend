package com.umc.finly.domain.market.repository.projection;

public interface StockEmotionBuyAggregation {

    Long getStockId();
    String getStockName();    //  종목 이름 가져오기
    String getEmotionCode();
    Long getBuyCount();
}
