package com.umc.finly.domain.analysis.stock.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@JsonPropertyOrder({
        "stockName",
        "emotion",
        "tradeType",
        "price",
        "date",
        "quantity",
        "decisionResult"
})
@Getter
@Builder
public class RecentDecisionResDTO {
    private String stockName;
    private String emotion;
    private String tradeType;
    private Integer price;
    private LocalDate date;
    private Integer quantity;
    private Integer decisionResult;
}
