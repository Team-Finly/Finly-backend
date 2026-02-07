package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.exception.KoreaInvestErrorCode;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 한국투자증권 API 접근 토큰 관리
 */
@Slf4j
@Component
public class KoreaInvestTokenManager {

    private final String appKey;
    private final String appSecret;
    private final String baseUrl;
    private final RestClient authRestClient;

    private String accessToken;
    private LocalDateTime expirationTime;

    public KoreaInvestTokenManager(
            @Value("${korea-invest.app-key}") String appKey,
            @Value("${korea-invest.app-secret}") String appSecret,
            @Value("${korea-invest.base-url}") String baseUrl) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.baseUrl = baseUrl;
        this.authRestClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public synchronized String getAccessToken() {
        // 메모리에 토큰과 만료시간이 있고, 아직 만료 전(5분 전 기준)이라면
        if (accessToken != null && expirationTime != null &&
                LocalDateTime.now().isBefore(expirationTime.minusMinutes(5))) {

            log.info("ℹ️ 기존 유효 토큰 사용 중 (만료 예정: {})",
                    expirationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return accessToken;
        }

        // 위 조건에 해당 안되면 갱신 실행
        refresh();
        return accessToken;
    }

    private void refresh() {
        log.info("🔄 한국투자증권 접근 토큰 갱신 시도 중...");

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", appKey);
        body.put("appsecret", appSecret);

        try {
            Map<String, Object> response = authRestClient.post()
                    .uri(baseUrl + "/oauth2/tokenP")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map>() {});

            if (response == null || !response.containsKey("access_token")) {
                log.error("❌ 토큰 발급 API 응답 이상: {}", response);
                throw new KoreaInvestException(KoreaInvestErrorCode.TOKEN_GENERATION_FAILED);
            }
            this.accessToken = (String) response.get("access_token");
            String expiredStr = (String) response.get("access_token_token_expired");
            this.expirationTime = LocalDateTime.parse(expiredStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("✅ 한국투자증권 토큰 갱신 완료! 만료 예정: {}", expiredStr);

        } catch (Exception e) {
                log.error("❌ 토큰 발급 중 오류 발생: {}", e.getMessage(), e);
                throw new KoreaInvestException(KoreaInvestErrorCode.TOKEN_GENERATION_FAILED, e.getMessage(), e);
        }
    }
}
