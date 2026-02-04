package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryRes;

public interface StockAnalysisService {
    StockSummaryRes getStockSummary(Long memberId, String symbol);
}
