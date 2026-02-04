package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.dto.response.StockSummaryRes;
import com.umc.finly.domain.market.exception.code.MarketErrorCode;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.TradeAction;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockAnalysisServiceImpl implements StockAnalysisService {
    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;
    private final StockPriceService stockPriceService;

    @Override
    public StockSummaryRes getStockSummary(Long memberId, String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));

        Long stockId = stock.getId();

        // record 조회
        List<RecordEntry> records =
                recordEntryRepository.findAllByMemberIdAndStockId(
                        memberId,
                        stockId
                );

        // 현재가 조회
        Integer currentPrice = stockPriceService.getCurrentPrice(symbol);

        if (records.isEmpty()) {
            return StockSummaryRes.builder()
                    .averageBuyPrice(0)
                    .currentPrice(currentPrice)
                    .totalBuyCount(0)
                    .maxHoldingDays(0)
                    .build();
        }

        // record_date 기준 오름차순 정렬
        // 같은 날짜이면 BUY -> SELL 순 정렬(날짜 단위 기록)
        records.sort(Comparator
                .comparing(RecordEntry::getRecordDate)
                .thenComparing(record ->
                        record.getTradeAction() == TradeAction.BUY ? 0 : 1
                )
        );

        // 평균 매수 가액(only BUY)
        BigDecimal totalAmount = BigDecimal.ZERO; // 총액
        BigDecimal totalQuantity = BigDecimal.ZERO;
        int totalBuyCount = 0;

        // 최대 보유 기간 계산
        BigDecimal holdingQuantity = BigDecimal.ZERO;
        LocalDate cycleStartDate = null;
        int maxHoldingDays = 0;

        for (RecordEntry record : records) {
            if (record.getTradeAction() == TradeAction.BUY) {
                totalBuyCount++;

                totalAmount = totalAmount.add(
                        record.getUnitPrice().multiply(record.getQuantity())
                );

                totalQuantity = totalQuantity.add(record.getQuantity());

                // 보유 수량이 0에서 양수로 바뀌면 새로운 보유 사이클 시작
                if (holdingQuantity.compareTo(BigDecimal.ZERO) == 0) {
                    cycleStartDate = record.getRecordDate();
                }

                holdingQuantity = holdingQuantity.add(record.getQuantity());
            }

            if (record.getTradeAction() == TradeAction.SELL) {
                holdingQuantity = holdingQuantity.subtract(record.getQuantity());

                // 보유 수량이 0이 되면 사이클 종료
                if (holdingQuantity.compareTo(BigDecimal.ZERO) == 0 && cycleStartDate != null) {
                    int days = (int) ChronoUnit.DAYS.between(
                            cycleStartDate,
                            record.getRecordDate()
                    );

                    maxHoldingDays = Math.max(maxHoldingDays, days);
                    cycleStartDate = null;
                }
            }
        }

        // 현재 보유 중인 경우
        if (holdingQuantity.compareTo(BigDecimal.ZERO) > 0 && cycleStartDate != null) {
            LocalDate endDate = LocalDate.now();

            int days = (int) ChronoUnit.DAYS.between(
                    cycleStartDate,
                    endDate
            );

            maxHoldingDays = Math.max(maxHoldingDays, days);
        }

        int averageBuyPrice = totalQuantity.compareTo(BigDecimal.ZERO) == 0
                ? 0
                : totalAmount
                    .divide(totalQuantity, 0, RoundingMode.HALF_UP)
                    .intValue();

        return StockSummaryRes.builder()
                .averageBuyPrice(averageBuyPrice)
                .currentPrice(currentPrice)
                .totalBuyCount(totalBuyCount)
                .maxHoldingDays(maxHoldingDays)
                .build();
    }
}
