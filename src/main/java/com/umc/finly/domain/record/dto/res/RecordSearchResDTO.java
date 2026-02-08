package com.umc.finly.domain.record.dto.res;

import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecordSearchResDTO {

    private List<SearchEntry> records;
    private int totalCount;

    @Getter
    @Builder
    public static class SearchEntry {
        private Long recordId;
        private LocalDate recordDate;
        private LocalDateTime recordedAt;
        private Session session;
        private TradeAction tradeAction;
        private String symbol;
        private BigDecimal unitPrice;
        private BigDecimal quantity;
        private EmotionCode emotionCode;
        private Integer emotionIntensity;
        private String memo;

        public static SearchEntry from(RecordEntry entry, String symbol) {
            return SearchEntry.builder()
                    .recordId(entry.getId())
                    .recordDate(entry.getRecordDate())
                    .recordedAt(entry.getCreatedAt())
                    .session(entry.getSession())
                    .tradeAction(entry.getTradeAction())
                    .symbol(symbol)
                    .unitPrice(entry.getUnitPrice())
                    .quantity(entry.getQuantity())
                    .emotionCode(entry.getEmotionCode())
                    .emotionIntensity(entry.getEmotionIntensity())
                    .memo(entry.getMemo())
                    .build();
        }
    }
}
