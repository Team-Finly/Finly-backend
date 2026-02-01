package com.umc.finly.domain.record.dto;

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
public class RecordDetailRes {

    private Long recordId;
    private LocalDate recordDate;
    private LocalDateTime recordedAt;
    private Session session;
    private TradeAction tradeAction;
    private Long stockId;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private EmotionCode emotionCode;
    private Integer emotionIntensity;
    private String memo;

    public static RecordDetailRes from(RecordEntry entry) {
        return RecordDetailRes.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
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
