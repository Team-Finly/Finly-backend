package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.infra.StockInfoFileParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StockInfoIntegrationTest {

    // @Autowired 대신 직접 생성 (스프링 컨테이너 비의존)
    private final StockInfoFileParser parser = new StockInfoFileParser();

    @Test
    @DisplayName("실제 MST 파일을 읽어 파싱 결과 확인 (로그 상세 출력)")
    void testWithRealFile() throws Exception {
        File file = new File("src/test/resources/kospi_code.mst");

        if (!file.exists()) {
            throw new RuntimeException("파일이 경로에 없습니다: " + file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            List<StockInfoDto> stocks = parser.parse(fis, MarketType.KOSPI);

            assertThat(stocks).isNotEmpty();

            System.out.println("================================================================");
            System.out.println("✅ 실제 파일 파싱 성공!");
            System.out.println("✅ 추출된 주식 종목 수: " + stocks.size());
            System.out.println("================================================================");

            // 전체 혹은 상위 10개만 상세 로그 출력
            stocks.stream().limit(10).forEach(s -> {
                System.out.println("[Market]   : " + s.getMarketType());
                System.out.println("[Symbol]   : " + s.getSymbol());
                System.out.println("[Name]     : " + s.getName());
                System.out.println("[ISIN]     : " + s.getIsin());
                // RawLine은 너무 길면 앞부분 100자만 출력 (필요시 전체 출력 가능)
                String rawDisplay = s.getRawLine().length() > 100
                        ? s.getRawLine().substring(0, 100) + "..."
                        : s.getRawLine();
                System.out.println("[RawLine]  : " + rawDisplay);
                System.out.println("----------------------------------------------------------------");
            });
        }
    }
}