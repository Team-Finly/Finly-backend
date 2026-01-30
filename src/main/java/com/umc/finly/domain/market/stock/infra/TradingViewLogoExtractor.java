package com.umc.finly.domain.market.stock.infra;

import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TradingView 심볼 페이지 HTML에서 로고 svg 파일명을 찾고,
 * S3 로고 base URL과 합쳐 최종 로고 URL을 만든다.
 */
@Component
public class TradingViewLogoExtractor {

    // 'logo-' 클래스가 포함된 태그 내의 src 속성값을 추출
    // [^>]+ : 태그가 닫히기 전까지의 모든 문자
    // src="([^"]+)" : src 안의 주소만 캡처
    private static final Pattern LOGO_TAG_PATTERN =
            Pattern.compile("class=\"[^\"]*logo-[^\"]*\"[^>]+src=\"(https://s3-symbol-logo\\.tradingview\\.com/[^\"]+)\"",
                    Pattern.CASE_INSENSITIVE);

    @Value("${tradingview.s3-logo-base-url}")
    private String logoBaseUrl;

    public String extractLogoUrl(String html) {
        Matcher matcher = LOGO_TAG_PATTERN.matcher(html);

        while (matcher.find()) {
            String fullUrl = matcher.group(1);

            // 시스템 아이콘(국가, 거래소 등)은 파일명에 'country/', 'source/'가 들어감
            if (!fullUrl.contains("country/") && !fullUrl.contains("source/") && !fullUrl.contains("indices/")) {
                return fullUrl; // 조건에 맞는 첫 번째 주소 반환
            }
        }

        throw new StockInfoException(
                StockInfoErrorCode.TRADINGVIEW_LOGO_NOT_FOUND,
                "TradingView 종목 로고 태그를 찾을 수 없습니다."
        );
    }
}