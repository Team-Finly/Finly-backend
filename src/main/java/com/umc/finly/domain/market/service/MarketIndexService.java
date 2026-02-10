package com.umc.finly.domain.market.service;

<<<<<<< Updated upstream
import com.umc.finly.domain.market.converter.MarketIndexConverter;
=======
>>>>>>> Stashed changes
import com.umc.finly.domain.market.dto.response.FearGreedResDTO;
import com.umc.finly.domain.market.dto.response.MarketIndexResDTO;
import com.umc.finly.domain.market.dto.response.MarketIndicesDTO;
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
    public MarketIndexResDTO getMarketIndex() {
        MarketIndicesDTO indices = marketIndexProvider.getMarketIndices();
        FearGreedResDTO fearGreed = fearGreedIndexProvider.getFearGreedIndex();
        LocalDateTime snapshotAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return MarketIndexConverter.toMarketIndexSnapshot(
                indices,
                fearGreed,
                snapshotAt
        );
    }
}
