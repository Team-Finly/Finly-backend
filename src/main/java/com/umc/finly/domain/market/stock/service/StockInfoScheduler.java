package com.umc.finly.domain.market.stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 매일 정해진 시간에 StockInfoSync + StockLogoUpdate 순서대로 실행하는 스케줄러.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockInfoScheduler {

    private final StockInfoSyncService stockInfoSyncService;
    private final StockLogoUpdateService stockLogoUpdateService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runDailyJob() {
        log.info("⚪ StockInfoScheduler 시작");

        try {
            // 1단계: KIS 종목정보 동기화
            stockInfoSyncService.syncDomesticStocks();

            // 2단계: TradingView 로고 URL 저장
            stockLogoUpdateService.updateMissingLogos();
        } catch (Exception e) {
            log.error("❗ 스케줄러 실행 중 장애 발생: {}", e.getMessage(), e);
        }
        log.info("✅ StockInfoScheduler 종료");
    }
}
