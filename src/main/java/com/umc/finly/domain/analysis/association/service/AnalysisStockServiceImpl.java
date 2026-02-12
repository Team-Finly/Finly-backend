
package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.response.AnalysisStockResDTO;
import com.umc.finly.domain.analysis.association.repository.AnalysisStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisStockServiceImpl implements AnalysisStockService {

    private final AnalysisStockRepository analysisStockRepository;

    @Override
    public List<AnalysisStockResDTO> getRecordedStocks(Long memberId) { //기록된 종목 조회
        return analysisStockRepository.findRecordedStocks(memberId);
    }

}
