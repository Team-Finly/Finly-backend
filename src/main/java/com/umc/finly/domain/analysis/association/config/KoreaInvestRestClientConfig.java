package com.umc.finly.domain.analysis.association.config;

import com.umc.finly.domain.analysis.association.infra.KoreaInvestHeaderInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 한국투자증권 API RestClient 설정
 */
@Configuration
public class KoreaInvestRestClientConfig {
    @Value("${korea-invest.base-url}")
    private String baseUrl;

    @Bean
    public RestClient koreaInvestRestClient(KoreaInvestHeaderInterceptor interceptor) {
        // 내부 HttpClient 설정
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)) // 연결 타임아웃 설정(5초)
                .build();
        // Factory에 HttpClient 주입
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5)); // 응답 타임아웃 설정(5초)

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .requestInterceptor(interceptor)
                .build();
    }
}
