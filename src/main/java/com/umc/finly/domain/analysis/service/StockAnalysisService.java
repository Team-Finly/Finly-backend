package com.umc.finly.domain.analysis.service;

import com.umc.finly.domain.analysis.dto.response.StockSummaryRes;

public interface StockAnalysisService {
    StockSummaryRes getStockSummary(String stockId);
}
