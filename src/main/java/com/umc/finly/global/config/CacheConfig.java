package com.umc.finly.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("marketIndex", "stockCurrentPrice");
        /**
         * 요청 1 -> 네이버 API 호출 -> 캐시 저장
         * 요청 2 (5초 이내) -> 캐시 반환
         * 요청 3 (5초 후) -> 다시 네이버 API 호출
         * 실시간 현재가는 아님 -> 네이버 API 과도 호출 방지 목적, 실시간 비슷하게 유지
         */
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.SECONDS)
                        .maximumSize(1000) // 최대 1000개 종목까지만 캐시, 메모리 사용량 통제
        );

        return cacheManager;
    }
}
