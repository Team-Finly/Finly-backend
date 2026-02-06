package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.StockRecordResDTO;
import java.util.List;

public interface AssociationAnalysisService {
    List<StockRecordResDTO> getRecordedStocks(Long memberId);

}