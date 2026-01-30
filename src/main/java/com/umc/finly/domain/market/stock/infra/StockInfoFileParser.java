package com.umc.finly.domain.market.stock.infra;

import com.umc.finly.domain.market.stock.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * KIS 종목정보파일(.mst) 전용 파서 클래스.
 */
@Component
public class StockInfoFileParser {
    private static final Charset KIS_CHARSET = Charset.forName("MS949");
    private static final String STOCK_GROUP_CODE = "ST";

    public List<StockInfoDto> parse(InputStream inputStream, MarketType marketType) {
        List<StockInfoDto> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, KIS_CHARSET))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < 40) continue; // 최소 길이 미달 제외

                byte[] bytes = line.getBytes(KIS_CHARSET);

                // 1. 단축코드(9), 표준코드(12) 추출
                String symbol = new String(bytes, 0, 9, KIS_CHARSET).trim();
                String isin = new String(bytes, 9, 12, KIS_CHARSET).trim();

                // 2. 종목명 추출 (21바이트 지점부터 40바이트만큼 읽음)
                String name = new String(bytes, 21, 40, KIS_CHARSET).trim();

                // 3. 그룹코드 추출 (종목명 영역 바로 뒤인 61바이트 지점에서 2바이트)
                String groupCode = new String(bytes, 61, 2, KIS_CHARSET).trim();

                // 4. 주식(ST) 필터링
                if (!STOCK_GROUP_CODE.equals(groupCode)) {
                    continue;
                }

                result.add(new StockInfoDto(
                        marketType,
                        symbol,
                        name,
                        isin,
                        line
                ));
            }
            return result;
        } catch (Exception e) {
            throw new StockInfoException(
                    StockInfoErrorCode.KIS_FILE_PARSE_FAILED,
                    "KIS 마스터 파일 파싱 실패",
                    e
            );
        }
    }
}