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
import java.util.List;

@Getter
@Builder
public class TodayRecordRes {

    private LocalDate date;
    private PrismFeedback prismFeedback;
    private List<TimelineEntry> timelineSummary;
    private boolean hasRecords;
    private int recordCount;

    @Getter
    @Builder
    public static class PrismFeedback {
        private String title;
        private LocalDateTime generatedAt;
    }

    @Getter
    @Builder
    public static class TimelineEntry {
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

        public static TimelineEntry from(RecordEntry entry, String symbol) {
            return TimelineEntry.builder()
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

    public static String generatePrismTitle(List<RecordEntry> entries) {
        if (entries.isEmpty()) {
            return "괜찮아요. 기록이 없는 날도 있을 수 있죠.";
        }
        if (entries.size() == 1) {
            EmotionCode emotion = entries.get(0).getEmotionCode();
            if (emotion == null) { // null 체크
                return "오늘 하루도 기록을 남겼네요!";
            }
            return "{{" + emotion.getLabel() + "}}한 하루네요!";
        }
        EmotionCode first = entries.get(0).getEmotionCode();
        EmotionCode last = entries.get(entries.size() - 1).getEmotionCode();
        if (first == null || last == null) { // null 체크
            return "오늘도 열심히 기록했네요!";
        }
        return "{{" + first.getLabel() + "}}하게 시작해 {{" + last.getLabel() + "}}" + last.getParticle() + " 마무리한 날이네요.";
    }
}
