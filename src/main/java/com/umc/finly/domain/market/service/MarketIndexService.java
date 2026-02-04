package com.umc.finly.domain.market.service;

import com.umc.finly.domain.market.dto.FearGreedRes;
import com.umc.finly.domain.market.dto.MarketIndexRes;
import com.umc.finly.domain.market.dto.MarketIndices;
import com.umc.finly.domain.market.infra.FearGreedIndexProvider;
import com.umc.finly.domain.market.infra.MarketIndexProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class MarketIndexService {
    private final MarketIndexProvider marketIndexProvider;
    private final FearGreedIndexProvider fearGreedIndexProvider;

    @Cacheable(
            value = "marketIndex",
            unless = "#result == null"
    )
    public MarketIndexRes getMarketIndex() {
        MarketIndices indices = marketIndexProvider.getMarketIndices();
        FearGreedRes fearGreed = fearGreedIndexProvider.getFearGreedIndex();
        LocalDateTime snapshotAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return MarketIndexRes.snapshot(
                indices.getKospi(),
                indices.getKosdaq(),
                fearGreed.getScore(),
                fearGreed.getStatus().name(),
                snapshotAt
        );
    }
}
