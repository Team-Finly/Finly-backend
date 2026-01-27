package com.umc.finly.domain.market.service;

import com.umc.finly.domain.market.dto.MarketIndexResponse;
import com.umc.finly.domain.market.infra.MarketIndexProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketIndexService {
    private final MarketIndexProvider marketIndexProvider;

    @Cacheable(
            value = "marketIndex",
            unless = "#result == null"
    )
    public MarketIndexResponse getMarketIndex() {
        return MarketIndexResponse.snapshot(
                marketIndexProvider.getKospi(),
                marketIndexProvider.getKosdaq()
        );
    }
}
