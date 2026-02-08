package com.umc.finly.domain.record.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

// OpenAI API 연동 설정
// RestClient Bean 생성 및 인증 헤더 설정
@Configuration
public class OpenAiConfig {

    // OpenAI API 키 (application.yml에서 주입)
    @Value("${openai.api-key}")
    private String apiKey;

    // API 요청 타임아웃 (기본 60초)
    @Value("${openai.timeout-seconds:60}")
    private int timeoutSeconds;

    // OpenAI API 호출용 RestClient Bean
    // JDK HttpClient 기반으로 타임아웃 설정 적용
    @Bean
    public RestClient openAiRestClient() {
        // HttpClient 생성 (연결 타임아웃 설정)
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();

        // RequestFactory 생성 (읽기 타임아웃 설정)
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));

        // RestClient 빌더로 기본 URL, 인증 헤더 설정
        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
