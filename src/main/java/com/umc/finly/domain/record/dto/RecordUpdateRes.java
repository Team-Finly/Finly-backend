package com.umc.finly.domain.record.dto;

import com.umc.finly.domain.record.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecordUpdateRes {

    private Long recordId;
    private LocalDate recordDate;
    private LocalDateTime updatedAt;
    private Session session;
    private TradeAction tradeAction;
    private Long stockId;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private EmotionCode emotionCode;
    private Integer emotionIntensity;
    private String memo;

    public static RecordUpdateRes from(RecordEntry entry) {
        return RecordUpdateRes.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .updatedAt(entry.getUpdatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .stockId(entry.getStockId())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }
}
