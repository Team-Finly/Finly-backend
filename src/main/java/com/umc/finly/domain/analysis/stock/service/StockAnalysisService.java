package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryResDTO;

public interface StockAnalysisService {
    StockSummaryResDTO getStockSummary(Long memberId, String symbol);
}
