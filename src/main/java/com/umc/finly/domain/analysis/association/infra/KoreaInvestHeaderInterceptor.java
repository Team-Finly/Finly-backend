package com.umc.finly.domain.analysis.association.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 한국투자증권 API 공통 요청 헤더 주입
 */
@Component
@RequiredArgsConstructor
public class KoreaInvestHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final KoreaInvestTokenManager tokenManager;

    @Value("${korea-invest.app-key}")
    private String appKey;
    @Value("${korea-invest.app-secret}")
    private String appSecret;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenManager.getAccessToken());
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("custtype", "P");
        return execution.execute(request, body);
    }
}
