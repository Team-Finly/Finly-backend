package com.umc.finly.domain.market.infra;

import java.math.BigDecimal;

public interface MarketIndexProvider {
    BigDecimal getKospi();
    BigDecimal getKosdaq();
}
