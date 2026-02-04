package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.infra.StockPriceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 주식 종목의 현재가를 조회하는 서비스
 * Spring Cache를 이용해 일정 시간 동안 캐싱하여 외부 API 호출 비용과 응답 지연을 줄이는 것을 목표로 함
 */
@Service
@RequiredArgsConstructor
public class StockPriceService {
    private final StockPriceProvider stockPriceProvider;

    /**
     * 메서드 호출 결과를 캐시에 저장해두고, 같은 조건의 호출이 들어오면
     * 메서드를 실행하지 않고 캐시 값을 바로 반환하는 어노테이션
     */
    @Cacheable(
            value = "stockCurrentPrice", // 현재가 캐시 이름
            key = "#stockCode",          // 종목 코드 기준으로 캐시
            unless = "#result == null"   // 조회 실패(null)는 캐시하지 않음
    )
    public Integer getCurrentPrice(String stockCode) {
        return stockPriceProvider.getCurrentPrice(stockCode);
    }
}
