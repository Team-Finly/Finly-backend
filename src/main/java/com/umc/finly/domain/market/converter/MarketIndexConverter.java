package com.umc.finly.domain.market.converter;

import com.umc.finly.domain.market.dto.response.FearGreedResDTO;
import com.umc.finly.domain.market.dto.response.MarketIndexResDTO;
import com.umc.finly.domain.market.dto.response.MarketIndicesDTO;

import java.time.LocalDateTime;

public class MarketIndexConverter {

    private MarketIndexConverter() {
    }

    public static MarketIndexResDTO toMarketIndexSnapshot(
            MarketIndicesDTO indices,
            FearGreedResDTO fearGreed,
            LocalDateTime snapshotAt
    ) {
        return MarketIndexResDTO.snapshot(
                indices.getKospi(),
                indices.getKosdaq(),
                fearGreed.getScore(),
                fearGreed.getStatus().name(),
                snapshotAt
        );
    }
}
