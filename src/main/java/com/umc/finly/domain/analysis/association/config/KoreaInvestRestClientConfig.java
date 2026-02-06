package com.umc.finly.domain.analysis.association.config;

import com.umc.finly.domain.analysis.association.infra.KoreaInvestHeaderInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 한국투자증권 API RestClient 설정
 */
@Configuration
public class KoreaInvestRestClientConfig {
    @Value("${korea-invest.base-url}")
    private String baseUrl;

    @Bean
    public RestClient koreaInvestRestClient(KoreaInvestHeaderInterceptor interceptor) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(interceptor)
                .build();
    }
}
