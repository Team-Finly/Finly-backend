package com.umc.finly.domain.analysis.service;

import com.umc.finly.domain.analysis.dto.response.StockSummaryRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockAnalysisServiceImpl implements StockAnalysisService {
    private final StockPriceService stockPriceService;

    @Override
    public StockSummaryRes getStockSummary(String stockId) {
        // TODO: 평균 매수 가액, 누적 매수 횟수, 최대 보유 기간 반환
        Integer currentPrice = stockPriceService.getCurrentPrice(stockId);

        return StockSummaryRes.currentPriceSummary(currentPrice);
    }
}
