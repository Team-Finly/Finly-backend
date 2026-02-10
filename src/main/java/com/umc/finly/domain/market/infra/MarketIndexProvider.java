package com.umc.finly.domain.market.infra;

import com.umc.finly.domain.market.dto.response.MarketIndicesDTO;

public interface MarketIndexProvider {
    MarketIndicesDTO getMarketIndices();
}
