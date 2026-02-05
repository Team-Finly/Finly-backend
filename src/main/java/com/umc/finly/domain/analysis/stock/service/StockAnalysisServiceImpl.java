package com.umc.finly.domain.analysis.stock.service;

import com.umc.finly.domain.analysis.stock.dto.res.PriceDistributionResDTO;
import com.umc.finly.domain.analysis.stock.dto.res.StockSummaryResDTO;
import com.umc.finly.domain.analysis.stock.enums.PriceRangeType;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockAnalysisServiceImpl implements StockAnalysisService {
    private static final int RANGE_PERCENT = 5;

    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;
    private final StockPriceService stockPriceService;

    @Override
    public StockSummaryResDTO getStockSummary(Long memberId, String symbol) {
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
            return StockSummaryResDTO.builder()
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

        return StockSummaryResDTO.builder()
                .averageBuyPrice(averageBuyPrice)
                .currentPrice(currentPrice)
                .totalBuyCount(totalBuyCount)
                .maxHoldingDays(maxHoldingDays)
                .build();
    }

    @Override
    public PriceDistributionResDTO getPriceDistribution(Long memberId, String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));

        Long stockId = stock.getId();

        // record 조회
        List<RecordEntry> records =
                recordEntryRepository.findAllByMemberIdAndStockIdAndTradeAction(memberId, stockId, TradeAction.BUY);

        if (records.isEmpty()) {
            return new PriceDistributionResDTO(
                    0,
                    new PriceDistributionResDTO.RangePolicy("AVERAGE_BUY_PRICE", RANGE_PERCENT),
                    List.of()
            );
        }

        // 평균 매수가
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;

        for (RecordEntry record : records) {
            totalAmount = totalAmount.add(record.getUnitPrice().multiply(record.getQuantity()));
            totalQuantity = totalQuantity.add(record.getQuantity());
        }

        int averageBuyPrice = totalAmount
                .divide(totalQuantity, 0, RoundingMode.HALF_UP)
                .intValue();

        // 구간 경계 계산
        int lowerBound = BigDecimal.valueOf(averageBuyPrice)
                .multiply(BigDecimal.valueOf(1 - RANGE_PERCENT / 100.0)) // 평균 매수가보다 RANGE_PERCENT% 낮은 가격
                .setScale(0, RoundingMode.HALF_UP) // 반올림
                .intValue();

        int upperBound = BigDecimal.valueOf(averageBuyPrice)
                .multiply(BigDecimal.valueOf(1 + RANGE_PERCENT / 100.0)) // 평균 매수가보다 RANGE_PERCENT% 높은 가격
                .setScale(0, RoundingMode.HALF_UP) // 반올림
                .intValue();

        int low = 0, mid = 0, high = 0; // LOW / MID / HIGH 개수(count)

        for (RecordEntry record : records) {
            int price = record.getUnitPrice().intValue();

            if (price < lowerBound) {
                low++;
            } else if (price <= upperBound) {
                mid++;
            } else {
                high++;
            }
        }

        int total = low + mid + high;

        int lowRatio = (low * 100) / total;
        int midRatio = (mid * 100) / total;
        int highRatio = (high * 100) / total;

        int sum = lowRatio + midRatio + highRatio;
        int remain = 100 - sum;

        int maxRatio = Math.max(lowRatio, Math.max(midRatio, highRatio));

        if (midRatio == maxRatio) { // 모두 동률일 경우 MID를 강조
            midRatio += remain;
        } else if (lowRatio == maxRatio) {
            lowRatio += remain;
        } else {
            highRatio += remain;
        }

        int finalMaxRatio = Math.max(lowRatio, Math.max(midRatio, highRatio));

        List<PriceDistributionResDTO.PriceDistributionItem> items = new ArrayList<>();

        items.add(new PriceDistributionResDTO.PriceDistributionItem(
                PriceRangeType.LOW,
                String.format("%,d원 미만", lowerBound),
                low,
                lowRatio,
                lowRatio == finalMaxRatio ? true : null
        ));

        items.add(new PriceDistributionResDTO.PriceDistributionItem(
                PriceRangeType.MID,
                String.format("%,d원 ~ %,d원", lowerBound, upperBound),
                mid,
                midRatio,
                midRatio == finalMaxRatio ? true : null
        ));

        items.add(new PriceDistributionResDTO.PriceDistributionItem(
                PriceRangeType.HIGH,
                String.format("%,d원 이상", upperBound),
                high,
                highRatio,
                highRatio == finalMaxRatio ? true : null
        ));

        return new PriceDistributionResDTO(
                averageBuyPrice,
                new PriceDistributionResDTO.RangePolicy("AVERAGE_BUY_PRICE", RANGE_PERCENT),
                items
        );
    }
}
