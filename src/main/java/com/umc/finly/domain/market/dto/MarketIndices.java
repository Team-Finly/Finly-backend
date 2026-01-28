package com.umc.finly.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MarketIndices {
    private final BigDecimal kospi;
    private final BigDecimal kosdaq;
}
