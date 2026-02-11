package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.response.HourlyChartResDTO;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        BigDecimal qty = record.getQuantity();
        BigDecimal unitPrice = record.getUnitPrice();
        BigDecimal totalPrice = (qty != null && unitPrice != null)
                ? unitPrice.multiply(qty)
                : null;

        // --- recordDateTime 결정 로직 ---
        LocalDateTime calculatedDateTime;
        LocalDate recordDate = record.getRecordDate();
        LocalDateTime createdAt = record.getCreatedAt();

        if (createdAt != null && recordDate.equals(createdAt.toLocalDate())) {
            // 날짜가 같은 경우: createdAt의 시간 유지 or 09:00 ~ 15:30 사이로 보정
            LocalTime createdTime = createdAt.toLocalTime();
            LocalTime marketOpen = LocalTime.of(9, 0);
            LocalTime marketClose = LocalTime.of(15, 30);

            if (createdTime.isBefore(marketOpen)) {
                calculatedDateTime = recordDate.atTime(marketOpen);
            } else if (createdTime.isAfter(marketClose)) {
                calculatedDateTime = recordDate.atTime(marketClose);
            } else {
                calculatedDateTime = createdAt;
            }
        } else {
            // 날짜가 다른 경우: session에 따라 시간 매핑
            calculatedDateTime = switch (record.getSession()) {
                case PRE_MARKET -> recordDate.atTime(9, 0);
                case MORNING -> recordDate.atTime(10, 30);
                case AFTERNOON -> recordDate.atTime(13, 30);
                case POST_MARKET -> recordDate.atTime(15, 30);
                default -> recordDate.atTime(9, 0); // 예외 처리
            };
        }

        // "yyyy-MM-dd HH:mm" 형식으로 포맷팅
        String formattedDateTime = calculatedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return HourlyChartResDTO.RecordData.builder()
                .recordId(record.getId())
                .recordDateTime(formattedDateTime)
                .emotionCode(record.getEmotionCode())
                .emotionIntensity(record.getEmotionIntensity())
                .tradeAction(record.getTradeAction())
                .quantity(qty)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .memo(record.getMemo())
                .finlyTalk(feedback != null ? feedback.getContent() : null)
                .build();
    }
}