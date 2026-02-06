package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.dto.res.PriceDistributionResDTO;
import com.umc.finly.domain.analysis.stock.dto.res.RecentDecisionResDTO;
import com.umc.finly.domain.analysis.stock.dto.res.StockSummaryResDTO;

import java.util.List;

public interface StockAnalysisService {
    StockSummaryResDTO getStockSummary(Long memberId, String symbol);
    PriceDistributionResDTO getPriceDistribution(Long memberId, String symbol);
    List<RecentDecisionResDTO> getRecentDecisions(Long memberId, String symbol, int limit);
}
