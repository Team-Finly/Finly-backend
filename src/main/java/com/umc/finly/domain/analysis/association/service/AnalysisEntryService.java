package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.AnalysisEntryConverter;
import com.umc.finly.domain.analysis.association.dto.AnalysisEntryResDTO;
import com.umc.finly.domain.analysis.association.exception.AnalysisEntryErrorCode;
import com.umc.finly.domain.analysis.association.exception.AnalysisEntryException;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisEntryService {

    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;

    public AnalysisEntryResDTO getEntryStatus(Long memberId) {
        Long recordCount = recordEntryRepository.countByMemberId(memberId);

        Stock topStock = null;

        if (recordCount >= 3) {
            // 기록 3개 이상일 때, 가장 많이 기록된 종목 id 1개 조회 (0번째 페이지에서 1개 가져오도록 요청)
            List<Long> topStockIds = recordEntryRepository.findTopStockByMemberId(memberId, PageRequest.of(0, 1));

            // 집계된 종목 id가 없을 때
            if (topStockIds.isEmpty()) {
                log.error("findTopStockByMemberId 결과가 비어있음. memberId={}, memberId");
                throw new AnalysisEntryException(AnalysisEntryErrorCode.TOP_STOCK_NOT_FOUND);
            }

            Long topStockId = topStockIds.get(0);

            // 찾은 종목 id로 조회했으나 해당 id를 가진 종목이 존재하지 않을 때
            topStock = stockRepository.findById(topStockId)
                    .orElseThrow(() -> {
                        log.error("findTopStockByMemberId 결과 추출된 topStockId({})를 가진 종목이 존재하지 않음.", topStockId);
                        return new AnalysisEntryException(AnalysisEntryErrorCode.TOP_STOCK_NOT_FOUND);
                    });
        }

        return AnalysisEntryConverter.toResDTO(recordCount, topStock);
    }
}
