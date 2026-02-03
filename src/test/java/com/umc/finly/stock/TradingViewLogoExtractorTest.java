package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.info.infra.TradingViewLogoExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TradingViewLogoExtractorTest {

    private TradingViewLogoExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new TradingViewLogoExtractor();
        // @Value 값을 수동으로 주입
        ReflectionTestUtils.setField(extractor, "logoBaseUrl", "https://s3-symbol-logo.tradingview.com/");
    }

    @Test
    @DisplayName("HTML 내에서 svg 파일명을 찾아 전체 URL을 생성한다")
    void extractLogoUrl_Success() {
        // given: 실제 트레이딩뷰 HTML 구조를 모사한 샘플
        String html = """
        <html>
            <body>
                <img class="logo-PsAlMQQF xlarge-PsAlMQQF small-F4HZNWkx letter-PsAlMQQF" 
                     src="https://s3-symbol-logo.tradingview.com/samsung-pharmaceutical--big.svg" 
                     alt="삼성제약">
            </body>
        </html>
        """;
        String symbol = "005930";

        // when
        String result = extractor.extractLogoUrl(html, symbol);

        // then
        assertThat(result).isEqualTo("https://s3-symbol-logo.tradingview.com/samsung-pharmaceutical--big.svg");    }
}
