package com.umc.finly.domain.market.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({ "kospi", "kosdaq", "fearGreed", "fearGreedStatus", "updatedAt" })
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketIndexRes {

    private BigDecimal kospi;
    private BigDecimal kosdaq;
    private Integer fearGreed;
    private String fearGreedStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static MarketIndexRes snapshot(
            BigDecimal kospi,
            BigDecimal kosdaq,
            Integer fearGreed,
            String fearGreedStatus,
            LocalDateTime updatedAt) {
        return MarketIndexRes.builder()
                .kospi(kospi)
                .kosdaq(kosdaq)
                .fearGreed(fearGreed)
                .fearGreedStatus(fearGreedStatus)
                .updatedAt(updatedAt)
                .build();
    }
}
