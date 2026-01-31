package com.umc.finly.domain.market.infra;

import com.umc.finly.domain.market.dto.MarketIndices;

public interface MarketIndexProvider {
    MarketIndices getMarketIndices();
}
