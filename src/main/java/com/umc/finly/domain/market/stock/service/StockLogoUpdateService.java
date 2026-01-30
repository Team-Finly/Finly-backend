package com.umc.finly.domain.market.stock.service;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import com.umc.finly.domain.market.stock.infra.TradingViewLogoExtractor;
import com.umc.finly.domain.market.stock.infra.TradingViewSymbolClient;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2단계: logoUrl이 null인 Stock 들에 대해 TradingView에서 로고 URL을 찾아 채우는 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockLogoUpdateService {

    private final StockRepository stockRepository;
    private final TradingViewSymbolClient tradingViewSymbolClient;
    private final TradingViewLogoExtractor tradingViewLogoExtractor;

    @Transactional
    public void updateMissingLogos() {
        List<Stock> targets = stockRepository.findByLogoUrlIsNull();

        int total = targets.size();
        int successCount = 0;
        int notFoundCount = 0;
        int failCount = 0;

        log.info("⚪ 로고 업데이트 시작: 총 {}건", total);

        for (Stock stock : targets) {
            String symbol = stock.getSymbol();
            try {
                // 1) 심볼 페이지 HTML 가져오기
                String html = tradingViewSymbolClient.fetchHtmlForKrSymbol(symbol);
                // 2) HTML에서 svg 파일명 찾아 logo URL 만들기
                String logoUrl = tradingViewLogoExtractor.extractLogoUrl(html, symbol);
                // 3) 엔티티에 반영
                stock.updateLogoUrl(logoUrl);

                log.info("✅ [{}] 로고 url 업데이트에 성공했습니다. {}", symbol, logoUrl);
            } catch (StockInfoException e) {
                // 로고를 못찾은 경우 (TradingView에 등록되어 있지 않은 종목이거나, 로고 이미지가 없는 종목인 경우)
                notFoundCount++;
                log.warn("⚠️ [{}] 로고를 찾을 수 없음 (StockInfoException: {})", symbol, e.getMessage());
            }

            catch (Exception e) {
                // 한 종목 실패해도 전체 배치가 죽지 않게 로그만 남기고 계속 진행
                log.warn("❗[{}] 로고 url 업데이트에 실패했습니다.", symbol, e);
            }
        }
        log.info("✅ 업데이트 완료 - 대상: {}건, 성공: {}건, 로고 없음: {}, 실패: {}건", total, successCount, notFoundCount, failCount);
    }
}
