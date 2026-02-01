package com.umc.finly.domain.market.stock.service;

import com.google.common.collect.Lists;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import com.umc.finly.domain.market.stock.infra.TradingViewLogoExtractor;
import com.umc.finly.domain.market.stock.infra.TradingViewSymbolClient;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void updateMissingLogos() {
        List<Stock> targets = stockRepository.findByLogoUrlIsNull();
        int total = targets.size();

        // 업데이트 대상이 없는 경우
        if (targets.isEmpty()) {
            log.info("⚪ 업데이트할 로고가 없습니다.");
            return;
        }

        // 업데이트 시작
        log.info("🚀 로고 업데이트 시작: 총 {}건", total);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger notFoundCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int partitionSize = 150;
        List<List<Stock>> partitions = Lists.partition(targets, partitionSize);

        partitions.parallelStream().forEach(batch -> {
             log.debug("🧵 [Thread: {}] {}건의 배치 처리 시작", Thread.currentThread().getName(), batch.size());

            for (Stock stock : batch) {
                // 작업 중단(Interrupt) 신호를 받으면 루프 종료
                if (Thread.currentThread().isInterrupted()) {
                    log.warn("🛑 로고 업데이트 중 인터럽트 감지로 인해 조기 종료합니다.");
                    break;
                }
                updateSingleStock(stock, successCount, notFoundCount, failCount);
            }

            try {
                // 한 배치가 끝나면 한꺼번에 DB 반영
                stockRepository.saveAllAndFlush(batch);
            } catch (Exception e) {
                log.error("❗ 로고 URL DB 저장 중 오류 발생 (Thread: {}): {}", Thread.currentThread().getName(), e);
            }
        });

        stopWatch.stop();

        // 성능 지표 계산 및 출력
        double totalSeconds = stopWatch.getTotalTimeSeconds();
        double tps = (totalSeconds > 0) ? (total / totalSeconds) : 0;

        log.info("✅ 로고 업데이트 완료 - 대상: {}건, 성공: {}건, 로고 없음: {}건, 실패: {}건",
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

            if (e.getErrorCode() == StockInfoErrorCode.TRADINGVIEW_SYMBOL_NOT_FOUND) {
                // TradingView에 해당 종목 페이지 자체가 없음 (미등록)
                log.warn("❌ [{}] TradingView 미등록 종목", symbol);
            } else if (e.getErrorCode() == StockInfoErrorCode.TRADINGVIEW_LOGO_NOT_FOUND) {
                // 페이지는 있으나 <img> 태그나 로고 주소가 없음
                log.warn("⚠️ [{}] 종목은 존재하나 로고 이미지를 찾을 수 없음", symbol);
            } else {
                log.warn("❓ [{}] 로고 업데이트 처리 오류: {}", symbol, e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail.incrementAndGet();
            log.error("❗ [{}] 로고 업데이트 중 인터럽트 발생", symbol, e);
        } catch (Exception e) {
            fail.incrementAndGet();
            log.error("❗ [{}] 로고 업데이트 시스템 에러: {}", symbol, e);
        }
    }
}