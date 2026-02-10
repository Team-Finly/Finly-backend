package com.umc.finly.domain.market.infra;

import com.umc.finly.domain.market.dto.response.FearGreedResDTO;

public interface FearGreedIndexProvider {
    FearGreedResDTO getFearGreedIndex();
}
