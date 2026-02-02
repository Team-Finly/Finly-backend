package com.umc.finly.domain.analysis.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 네이버 주식 API 호출을 위한 RestClient 설정 클래스
 * 외부 API 호출 시 타임아웃을 설정하여 무한 대기 상태로 인한 장애를 방지
 */
@Configuration
public class NaverStockRestClientConfig {
    @Bean
    @Primary
    public RestClient naverStockRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)) // 연결 타임아웃
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5)); // 응답 타임아웃

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
