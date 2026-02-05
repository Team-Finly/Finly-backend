package com.umc.finly.domain.home.dto;
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
public class HomeRecordsRes {

    private List<Record> records;

    @Getter
    @Builder
    public static class Record {

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
    }

    public static HomeRecordsRes from(List<Record> records) {
        return HomeRecordsRes.builder()
                .records(records)
                .build();
    }
}
