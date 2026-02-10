package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.response.AnalysisEntryResDTO;
import com.umc.finly.domain.analysis.association.dto.response.AnalysisEntryResDTO.RecordLevel;
import com.umc.finly.domain.analysis.association.dto.response.AnalysisEntryResDTO.DefaultStockDto;

import com.umc.finly.domain.market.stock.entity.Stock;

public class AnalysisEntryConverter {

    /**
     * recordCount, 대표 종목 엔티티를 받아서 ResDTO 생성
     * - recordCount 0  -> NONE, defaultStock = null
     * - recordCount 1~2 -> LOW, defaultStock = null
     * - recordCount 3 이상 -> HIGH, defaultStock = topStock 기반 DTO
     */
    public static AnalysisEntryResDTO toResDTO (long recordCount, Stock topStock) {
        RecordLevel level = RecordLevel.fromRecordCount(recordCount);

        DefaultStockDto defaultStockDto = null;
        if (level == RecordLevel.HIGH && topStock != null) {
            defaultStockDto = toDefaultStock(topStock);
        }
        return AnalysisEntryResDTO.builder()
                .recordLevel(level)
                .totalRecordCount(recordCount)
                .defaultStock(defaultStockDto)
                .build();

    }

    /**
     * Stock 엔티티 -> DefaultStockDto
     */
    public static DefaultStockDto toDefaultStock(Stock stock) {
        if (stock == null) {
            return null;
        }
        return DefaultStockDto.builder()
                .stockId(stock.getId())
                .symbol(stock.getSymbol())
                .name(stock.getName())
                .build();
    }
}
