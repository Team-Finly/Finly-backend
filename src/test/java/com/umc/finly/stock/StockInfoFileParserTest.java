package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.info.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.info.infra.StockInfoFileParser;
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
        // 1. 단축코드 (9자리) - 예: "455900   "
        String symbolPart = String.format("%-9s", symbol);

        // 2. 표준코드 (12자리) - 예: "KR7455900001"
        String isinPart = String.format("%-12s", isin);

        // 3. 종목명 (40자리)
        // 한글이 섞여있으므로 단순히 %-40s를 쓰면 바이트 수가 틀어집니다.
        // 아래 별도 메서드로 바이트 길이를 맞춰야 합니다.
        String namePart = formatFixedByteLength(name, 40);

        // 4. 그룹코드(ST 등) 및 나머지 (KIS 명세 기준 보통 한 줄은 200~300바이트 이상)
        String groupPart = String.format("%-2s", groupCode);

        // 5. 나머지 더미 데이터
        String dummy = "2100910270000 NN N    N  0  N    N0000373500000100001NNN00NNN000000100N0900000004150390000000005002024032600000000001521800000000000760902650012       0 NN000000042-00000108-00000100-0100-0000004120241231000005683   NNN";

        return symbolPart + isinPart + namePart + groupPart + dummy;
    }

    // 한글/영문 섞인 문자열을 고정 바이트 길이로 맞추는 유틸리티
    private String formatFixedByteLength(String text, int length) {
        StringBuilder sb = new StringBuilder(text);
        int currentByteLength = text.getBytes(Charset.forName("MS949")).length;

        while (currentByteLength < length) {
            sb.append(" ");
            currentByteLength++;
        }
        return sb.toString();
    }
}