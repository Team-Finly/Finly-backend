package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.dto.res.PriceDistributionResDTO;
import com.umc.finly.domain.analysis.stock.dto.res.StockSummaryResDTO;

public interface StockAnalysisService {
    StockSummaryResDTO getStockSummary(Long memberId, String symbol);
    PriceDistributionResDTO getPriceDistribution(Long memberId, String symbol);
}
