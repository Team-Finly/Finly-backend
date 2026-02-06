package com.umc.finly.domain.market.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MarketIndicesDTO {
    private final BigDecimal kospi;
    private final BigDecimal kosdaq;
}
