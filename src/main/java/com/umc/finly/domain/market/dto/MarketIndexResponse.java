package com.umc.finly.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketIndexResponse {

    private Double kospi;
    private Double kosdaq;
    private Integer fearGreed;
    private String fearGreedStatus;
    private String updatedAt;
}
