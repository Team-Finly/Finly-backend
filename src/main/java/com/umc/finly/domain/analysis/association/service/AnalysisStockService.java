package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.AnalysisStockResDTO;
import java.util.List;

public interface AnalysisStockService {
    List<AnalysisStockResDTO> getRecordedStocks(Long memberId);

}