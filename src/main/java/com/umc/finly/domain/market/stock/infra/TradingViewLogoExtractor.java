package com.umc.finly.domain.market.stock.infra;

import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * TradingView 심볼 페이지 HTML에서 로고 svg 파일명을 찾고,
 * S3 로고 base URL과 합쳐 최종 로고 URL을 만든다.
 */


@Component
public class TradingViewLogoExtractor {

    @Value("${tradingview.s3-logo-base-url}")
    private String logoBaseUrl;

    public String extractLogoUrl(String html, String symbol) {
        // 1. HTML 파싱
        Document doc = Jsoup.parse(html);

        // 2. CSS 선택자로 이미지 태그 추출
        // [class*=logo-] : 클래스명에 "logo-"가 포함된 모든 img 태그
        Elements imgTags = doc.select("img[class*=logo-]");

        for (Element img : imgTags) {
            String rawUrl = img.attr("src");

            if (rawUrl.isEmpty()) continue;

            // 3. URL 정규화 (절대 경로 처리)
            String fullUrl = formatUrl(rawUrl);

            // 4. 필터링 (시스템 아이콘 제외 및 베이스 URL 확인)
            if (fullUrl.startsWith(logoBaseUrl) && !isSystemIcon(fullUrl)) {
                return fullUrl;
            }
        }

        throw new StockInfoException(StockInfoErrorCode.TRADINGVIEW_LOGO_NOT_FOUND, symbol);
    }

    private String formatUrl(String rawUrl) {
        if (rawUrl.startsWith("http")) return rawUrl;

        String base = logoBaseUrl.endsWith("/") ? logoBaseUrl : logoBaseUrl + "/";
        String path = rawUrl.startsWith("/") ? rawUrl.substring(1) : rawUrl;
        return base + path;
    }

    private boolean isSystemIcon(String url) {
        return url.contains("country/") || url.contains("source/") || url.contains("indices/");
    }
}