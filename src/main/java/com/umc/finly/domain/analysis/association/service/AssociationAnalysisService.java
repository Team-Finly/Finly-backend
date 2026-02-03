package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.StockRecordRes;
import java.util.List;

public interface AssociationAnalysisService {
        List<StockRecordRes> getRecordedStocks(Long memberId);

}
