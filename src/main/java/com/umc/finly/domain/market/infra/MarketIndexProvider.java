package com.umc.finly.domain.market.infra;

import com.umc.finly.domain.market.dto.res.MarketIndicesDTO;

public interface MarketIndexProvider {
    MarketIndicesDTO getMarketIndices();
}
