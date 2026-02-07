package com.umc.finly.domain.record.dto.res;

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
public class RecordUpdateResDTO {

    private Long recordId;
    private LocalDate recordDate;
    private LocalDateTime updatedAt;
    private Session session;
    private TradeAction tradeAction;
    private String symbol;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private EmotionCode emotionCode;
    private Integer emotionIntensity;
    private String memo;

    public static RecordUpdateResDTO from(RecordEntry entry, Stock stock) {
        return RecordUpdateResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .updatedAt(entry.getUpdatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(stock.getSymbol())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }
}
