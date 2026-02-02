package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.infra.TradingViewLogoExtractor;
import com.umc.finly.domain.market.stock.infra.TradingViewSymbolClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TradingViewSymbolClient.class, TradingViewLogoExtractor.class})
@ActiveProfiles("test")
// RUN_EXTERNAL_TESTS 값이 true일 때만 실행
//@EnabledIfEnvironmentVariable(named = "RUN_EXTERNAL_TESTS", matches = "true")
class TradingViewIntegrationTest {

    @Autowired
    private TradingViewSymbolClient symbolClient;

    @Autowired
    private TradingViewLogoExtractor logoExtractor;

    @Test
    @DisplayName("삼성제약(001360) 심볼로 실제 트레이딩뷰에서 로고 URL을 추출한다")
    void fetchRealLogoUrl() {
        String symbol = "001360";

        // 1. HTML 가져오기
        String html = symbolClient.fetchHtmlForKrSymbol(symbol);
        assertThat(html).isNotBlank();

        // 2. 로고 추출하기
        String logoUrl = logoExtractor.extractLogoUrl(html, symbol);

        System.out.println("✅ 추출된 로고 URL: " + logoUrl);
        assertThat(logoUrl).contains(".svg");
    }
}
