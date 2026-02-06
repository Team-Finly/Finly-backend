
package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.StockRecordResDTO;
import com.umc.finly.domain.analysis.association.repository.AssociationAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssociationAnalysisServiceImpl implements AssociationAnalysisService {

    private final AssociationAnalysisRepository associationAnalysisRepository;

    @Override
    public List<StockRecordResDTO> getRecordedStocks(Long memberId) { //기록된 종목 조회
        return associationAnalysisRepository.findRecordedStocks(memberId);
    }

}
