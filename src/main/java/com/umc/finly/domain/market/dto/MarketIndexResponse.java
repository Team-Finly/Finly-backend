package com.umc.finly.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketIndexResponse {

    private BigDecimal kospi;
    private BigDecimal kosdaq;
    private Integer fearGreed;
    private String fearGreedStatus;
    private String updatedAt;
}
