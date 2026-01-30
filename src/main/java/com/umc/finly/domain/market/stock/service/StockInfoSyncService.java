package com.umc.finly.domain.market.stock.service;

import com.umc.finly.domain.market.stock.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import com.umc.finly.domain.market.stock.infra.KisStockInfoFileClient;
import com.umc.finly.domain.market.stock.infra.StockInfoFileParser;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoSyncService {

    private final KisStockInfoFileClient kisStockInfoFileClient;
    private final StockInfoFileParser stockInfoFileParser;
    private final StockRepository stockRepository;

    @Transactional
    public void syncDomesticStocks() {
        log.info(">>> ⚪ 국내 종목 정보 동기화 시작");
        List<StockInfoDto> allInfos = new ArrayList<>();

        // 1. 코스피 데이터 처리
        try (InputStream is = kisStockInfoFileClient.downloadKospi()) {
            List<StockInfoDto> kospiList = stockInfoFileParser.parse(is, MarketType.KOSPI);
            allInfos.addAll(kospiList);
            log.info("✅ KOSPI 다운로드 및 파싱 완료: {} 건", kospiList.size());
        } catch (Exception e) {
            log.error("❗ KOSPI 동기화 중 오류: {}", e.getMessage());
            throw new StockInfoException(StockInfoErrorCode.STOCK_INFO_SYNC_FAILED, e.getMessage());
        }

        // 2. 코스닥 데이터 처리
        try (InputStream is = kisStockInfoFileClient.downloadKosdaq()) {
            List<StockInfoDto> kosdaqList = stockInfoFileParser.parse(is, MarketType.KOSDAQ);
            allInfos.addAll(kosdaqList);
            log.info("✅ KOSDAQ 다운로드 및 파싱 완료: {} 건", kosdaqList.size());
        } catch (Exception e) {
            log.error("❗ KOSDAQ 동기화 중 오류: {}", e.getMessage());
            throw new StockInfoException(StockInfoErrorCode.STOCK_INFO_SYNC_FAILED, e.getMessage());
        }

        // 3. 통합 처리 (Upsert & Deactivate)
        processStocks(allInfos);
        log.info(">>> ✅ 국내 종목 정보 동기화 최종 완료");
    }

    private void processStocks(List<StockInfoDto> allInfos) {
        // DB에 있는 기존 모든 종목을 Symbol 기준으로 Map 생성
        Map<String, Stock> existingStockMap = stockRepository.findAll().stream()
                .collect(Collectors.toMap(Stock::getSymbol, stock -> stock));

        // 이번 파일에 포함된 Symbol 세트
        Set<String> newSymbolSet = allInfos.stream()
                .map(StockInfoDto::getSymbol)
                .collect(Collectors.toSet());

        log.info("\uD83D\uDD35 DB 기존 종목 수: {} 건", existingStockMap.size());

        // 1. 신규 추가 및 기존 정보 업데이트
        for (StockInfoDto info : allInfos) {
            Stock stock = existingStockMap.get(info.getSymbol());

            if (stock != null) {
                // [CASE 1 & 2] 기존 종목 존재 -> 정보 업데이트
                stock.updateInfo(info.getMarketType(), info.getSymbol(), info.getName(), info.getIsin());

                if (stock.getIsActive()) {
                    // [CASE 1] 기존에 활성 상태였던 종목
                    log.info(">>> \uD83D\uDD35 [업데이트] 기존 종목 정보 갱신: {} ({})", info.getName(), info.getSymbol());
                } else {
                    // [CASE 2] 기존에 비활성 상태였다가 다시 파일에 등장 (재상장 등)
                    stock.activate();
                    log.info(">>> \uD83D\uDD35 [재활성화] 비활성 종목 다시 활성화: {} ({})", info.getName(), info.getSymbol());
                }
            } else {
                // [CASE 3] 신규 종목 -> 생성
                Stock newStock = Stock.builder()
                        .marketType(info.getMarketType())
                        .symbol(info.getSymbol())
                        .name(info.getName())
                        .isin(info.getIsin())
                        .isActive(true)
                        .build();

                stockRepository.save(newStock);
                log.info(">>> \uD83D\uDD35 [신규 등록] 새로운 종목 발견 및 등록: {} ({})", info.getName(), info.getSymbol());
            }
        }

        // 2. 파일에 없는 종목 deactivate
        List<Stock> toDeactivate = existingStockMap.values().stream()
                .filter(stock -> !newSymbolSet.contains(stock.getSymbol()))
                .filter(Stock::getIsActive) // 이미 꺼진 건 제외
                .toList();

        toDeactivate.forEach(stock -> {
            log.info("\uD83D\uDFE2 상장 폐지 혹은 제외 종목 비활성화: {} ({})", stock.getName(), stock.getSymbol());
            stock.deactivate();
        });

        log.info("☑\uFE0F 신규/업데이트 처리 완료, 비활성화 처리 수: {} 건", toDeactivate.size());
    }
}