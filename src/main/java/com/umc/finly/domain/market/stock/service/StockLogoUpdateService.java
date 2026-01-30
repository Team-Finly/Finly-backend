package com.umc.finly.domain.market.stock.service;

import com.google.common.collect.Lists;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import com.umc.finly.domain.market.stock.infra.TradingViewLogoExtractor;
import com.umc.finly.domain.market.stock.infra.TradingViewSymbolClient;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockLogoUpdateService {

    private final StockRepository stockRepository;
    private final TradingViewSymbolClient tradingViewSymbolClient;
    private final TradingViewLogoExtractor tradingViewLogoExtractor;

    @Async("logoUpdateExecutor")
    public void updateMissingLogos() {
        List<Stock> targets = stockRepository.findByLogoUrlIsNull();
        int total = targets.size();

        // 1. 업데이트 대상이 없는 경우
        if (targets.isEmpty()) {
            log.info("⚪ 업데이트할 로고가 없습니다.");
            return;
        }

        // 2. 업데이트 시작
        log.info("🚀 로고 업데이트 시작: 총 {}건", total);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger notFoundCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int partitionSize = 150;
        List<List<Stock>> partitions = Lists.partition(targets, partitionSize);

        // 3. 병렬 처리 시작
        partitions.parallelStream().forEach(batch -> {
            // 현재 어떤 스레드가 이 배치를 가져갔는지 확인
            log.debug("🧵 [Thread: {}] {}건의 배치 처리 시작", Thread.currentThread().getName(), batch.size());

            for (Stock stock : batch) {
                updateSingleStock(stock, successCount, notFoundCount, failCount);
            }

            try {
                // 4. DB 반영
                stockRepository.saveAllAndFlush(batch);
            } catch (Exception e) {
                log.error("❗ [Thread: {}] DB 저장 중 오류 발생: {}", Thread.currentThread().getName(), e.getMessage());
            }
        });

        stopWatch.stop();

        // 5. 성능 지표 계산 및 출력
        double totalSeconds = stopWatch.getTotalTimeSeconds();
        double tps = (totalSeconds > 0) ? (total / totalSeconds) : 0;

        log.info("✅ 업데이트 완료 - 대상: {}건, 성공: {}건, 로고 없음: {}건, 실패: {}건",
                total, successCount.get(), notFoundCount.get(), failCount.get());

        log.info("📊 [StockLogoUpdate 작업]");
        log.info(">> 총 소요 시간: {}s", String.format("%.2f", totalSeconds));
        log.info(">> 초당 처리량(TPS): {}건/sec", String.format("%.2f", tps));
        log.info(">> 병렬 처리 방식: ParallelStream (Partition Size: {})", partitionSize);
    }

    private void updateSingleStock(Stock stock, AtomicInteger success, AtomicInteger notFound, AtomicInteger fail) {
        String symbol = stock.getSymbol();
        try {
            // 스레드별 동작 확인용
            log.debug("🔍 [Thread: {}] 처리 중: {}", Thread.currentThread().getName(), symbol);

            String html = tradingViewSymbolClient.fetchHtmlForKrSymbol(symbol);
            String logoUrl = tradingViewLogoExtractor.extractLogoUrl(html, symbol);

            stock.updateLogoUrl(logoUrl);
            success.incrementAndGet();

            log.info("✅ [{}] 로고 업데이트 완료 (현재 성공: {}건)", symbol, success.get());

            // 외부 서버 차단 방지를 위한 미세 지연
            Thread.sleep(100);

        } catch (StockInfoException e) {
            notFound.incrementAndGet();

            Throwable rootCause = e.getCause();
            while (rootCause != null && rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

            String detail = (rootCause != null) ? rootCause.getMessage() : e.getMessage();

            log.warn("⚠️ [{}] 로고 업데이트 실패 | 원인: {}", symbol, detail);

        } catch (Exception e) {
            fail.incrementAndGet();
            log.error("❗ [{}] 시스템 에러: {}", symbol, e.getMessage());
        }
    }
}