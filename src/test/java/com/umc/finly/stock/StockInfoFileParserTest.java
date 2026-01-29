package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.infra.StockInfoFileParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StockInfoFileParserTest {

    private StockInfoFileParser parser;
    private final Charset KIS_CHARSET = Charset.forName("MS949");

    @BeforeEach
    void setUp() {
        parser = new StockInfoFileParser();
    }

    @Test
    @DisplayName("가변 길이 종목명 파싱 및 로그 확인")
    void testVariableLengthNameParsing() throws Exception {
        // Given
        String line1 = createMockRawLine("000020", "KR7000020008", "동화약품", "ST");
        String line2 = createMockRawLine("999999", "KR7999999999", "우주항공메타버스환경그린에너지우선주", "ST");

        String content = line1 + "\n" + line2;
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(KIS_CHARSET));

        // When
        List<StockInfoDto> result = parser.parse(inputStream, MarketType.KOSPI);

        // [LOG] 콘솔 출력
        System.out.println("========= 파싱 결과 확인 =========");
        result.forEach(dto -> {
            System.out.println("단축코드: " + dto.getSymbol());
            System.out.println("종목명  : " + dto.getName());
            System.out.println("원본줄  : " + dto.getRawLine());
            System.out.println("--------------------------------");
        });

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("동화약품");
        assertThat(result.get(1).getName()).isEqualTo("우주항공메타버스환경그린에너지우선주");
    }

    @Test
    @DisplayName("그룹코드 필터링: ST만 포함하고 나머지는 제외한다")
    void testGroupCodeFiltering() throws Exception {
        // Given: 주식(ST), ETF(EF), ETN(EN) 각각 1개씩
        String stock = createMockRawLine("005930", "KR7059300003", "삼성전자", "ST");
        String etf = createMockRawLine("122630", "KR7122630007", "KODEX레버리지", "EF");
        String etn = createMockRawLine("500001", "KRE500001001", "신한코스피ETN", "EN");

        String content = stock + "\n" + etf + "\n" + etn;
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(KIS_CHARSET));

        // When
        List<StockInfoDto> result = parser.parse(inputStream, MarketType.KOSPI);

        // [LOG] 콘솔 출력
        System.out.println("========= 필터링 결과 확인 =========");
        result.forEach(dto -> System.out.println("추출된 종목: " + dto.getName() + " (그룹코드: ST)"));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSymbol()).isEqualTo("005930");
    }

    private String createMockRawLine(String symbol, String isin, String name, String groupCode) {
        // 단축(9) + 표준(12) + 명칭 + 고정부(그룹코드2 + 나머지226)
        String head = String.format("%-9s%-12s%s", symbol, isin, name);
        String tail = groupCode + " ".repeat(226);
        return head + tail;
    }
}