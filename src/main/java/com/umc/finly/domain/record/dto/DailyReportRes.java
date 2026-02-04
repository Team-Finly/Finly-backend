package com.umc.finly.domain.record.dto;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class DailyReportRes {

    private Long recordId;
    private LocalDate recordDate;
    private LocalDateTime recordedAt;
    private Session session;
    private TradeAction tradeAction;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private EmotionCode emotionCode;
    private String name;
    private String content;

    public static DailyReportRes from(RecordEntry entry, Stock stock, String content) {
        return DailyReportRes.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .name(stock.getName())
                .content(content)
                .build();
    }
}