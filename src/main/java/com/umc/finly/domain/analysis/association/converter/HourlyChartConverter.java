package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.HourlyChartResDTO;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HourlyChartConverter {

    public static HourlyChartResDTO toHourlyChartResDTO(Stock stock, LocalDate targetDate,
                                                        List<HourlyChartResDTO.PriceData> prices,
                                                        List<RecordEntry> records,
                                                        Map<Long, RecordFeedback> feedbackMap) {
        return HourlyChartResDTO.builder()
                .stockId(stock.getId())
                .stockCode(stock.getSymbol())
                .stockName(stock.getName())
                .now(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .targetDate(targetDate)
                .prices(prices)
                .records(records.stream()
                        .map(r -> toRecordData(r, feedbackMap.get(r.getId()))) // RecordEntry ID로 Map 조회
                        .toList())
                .build();
    }

    private static HourlyChartResDTO.RecordData toRecordData(RecordEntry record, RecordFeedback feedback) {
        BigDecimal qty = record.getQuantity() != null ? record.getQuantity() : BigDecimal.ZERO;
        BigDecimal unitPrice = record.getUnitPrice() != null ? record.getUnitPrice() : BigDecimal.ZERO;

        return HourlyChartResDTO.RecordData.builder()
                .recordId(record.getId())
                .recordDate(record.getRecordDate())
                .emotionCode(record.getEmotionCode())
                .emotionIntensity(record.getEmotionIntensity())
                .tradeAction(record.getTradeAction())
                .quantity(qty)
                .unitPrice(unitPrice)
                .totalPrice(unitPrice.multiply(qty))
                .memo(record.getMemo())
                .finlyTalk(feedback != null ? feedback.getContent() : null)
                .build();
    }
}