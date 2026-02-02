package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.infra.StockInfoFileParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {StockInfoFileParser.class}) // 필요한 빈만 선택적 로드
@ActiveProfiles("test")
class StockInfoIntegrationTest {

    @Autowired
    private StockInfoFileParser parser;

    @Test
    @DisplayName("실제 MST 파일을 읽어 파싱 결과 확인")
    void testWithRealFile() throws Exception {

        File file = new File("src/test/resources/kospi_code.mst");

        if (!file.exists()) {
            throw new RuntimeException("파일이 경로에 없습니다: " + file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            // 파싱 수행
            List<StockInfoDto> stocks = parser.parse(fis, MarketType.KOSPI);

            // 검증 및 로그 출력
            assertThat(stocks).isNotEmpty();

            System.out.println("================================================================");
            System.out.println("✅ 컨테이너 로드 및 파싱 성공!");
            System.out.println("✅ 추출된 주식 종목 수: " + stocks.size());
            System.out.println("================================================================");

            stocks.stream().limit(5).forEach(s -> {
                System.out.println(String.format("[Market: %s] [Code: %s] [Name: %s] [ISIN: %s]",
                        s.getMarketType(), s.getSymbol(), s.getName(), s.getIsin()));
            });
        }
    }
}