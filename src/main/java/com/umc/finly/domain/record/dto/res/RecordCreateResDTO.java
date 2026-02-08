package com.umc.finly.domain.record.dto.res;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecordCreateResDTO {

    private Long recordId;
    private LocalDate recordDate;
    private LocalDateTime recordedAt;
    private Session session;
    private TradeAction tradeAction;
    private String symbol;
    private EmotionCode emotionCode;
    private Integer emotionIntensity;
    private String memo;
    private FeedbackInfo feedback;

    @Getter
    @Builder
    public static class FeedbackInfo {
        private Long feedbackId;
        private String status;
    }

    public static RecordCreateResDTO from(RecordEntry entry, Stock stock, RecordFeedback feedback) {
        FeedbackInfo feedbackInfo = null;
        if (feedback != null) {
            feedbackInfo = FeedbackInfo.builder()
                    .feedbackId(feedback.getId())
                    .status(feedback.getStatus().name())
                    .build();
        }

        return RecordCreateResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(stock.getSymbol())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .feedback(feedbackInfo)
                .build();
    }
}
