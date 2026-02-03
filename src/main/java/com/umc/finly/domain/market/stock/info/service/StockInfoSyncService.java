package com.umc.finly.domain.market.stock.info.service;

import com.umc.finly.domain.market.stock.info.dto.StockAdminResponse;
import com.umc.finly.domain.market.stock.info.dto.StockInfoDto;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.info.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.info.exception.StockInfoException;
import com.umc.finly.domain.market.stock.info.infra.KisStockInfoFileClient;
import com.umc.finly.domain.market.stock.info.infra.StockInfoFileParser;
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

    public StockAdminResponse.StockSync syncDomesticStocks() {
        log.info(">>> ⚪ 국내 종목 정보 동기화 시작");
        List<StockInfoDto> allInfos = new ArrayList<>();

        int kospiCount = 0;
        int kosdaqCount = 0;

        // 1. 코스피 데이터 처리
        try (InputStream is = kisStockInfoFileClient.downloadKospi()) {
            List<StockInfoDto> kospiList = stockInfoFileParser.parse(is, MarketType.KOSPI);
            kospiCount = kospiList.size();
            allInfos.addAll(kospiList);
            log.info("✅ KOSPI 다운로드 및 파싱 완료: {} 건", kospiCount);
        } catch (Exception e) {
            log.error("❗ KOSPI 동기화 중 오류", e);
            throw new StockInfoException(StockInfoErrorCode.STOCK_INFO_SYNC_FAILED, e.getMessage(), e);
        }

        // 2. 코스닥 데이터 처리
        try (InputStream is = kisStockInfoFileClient.downloadKosdaq()) {
            List<StockInfoDto> kosdaqList = stockInfoFileParser.parse(is, MarketType.KOSDAQ);
            kosdaqCount = kosdaqList.size();
            allInfos.addAll(kosdaqList);
            log.info("✅ KOSDAQ 다운로드 및 파싱 완료: {} 건", kosdaqCount);
        } catch (Exception e) {
            log.error("❗ KOSDAQ 동기화 중 오류", e);
            throw new StockInfoException(StockInfoErrorCode.STOCK_INFO_SYNC_FAILED, e.getMessage(), e);
        }

        if (kospiCount == 0 || kosdaqCount == 0) {
            log.error("❗ 종목 정보 파싱 결과가 비어있습니다. (KOSPI: {}건, KOSDAQ: {}건)", kospiCount, kosdaqCount);
            throw new StockInfoException(StockInfoErrorCode.STOCK_INFO_SYNC_FAILED, "KIS 종목 정보 파일 파싱 결과가 비어 있어 동기화를 중단합니다.");
        }
        // 3. 통합 처리 (Upsert & Deactivate)
        int deactivatedCount = processStocks(allInfos, kospiCount, kosdaqCount);

        // 4. DTO 응답용 리스트 생성
        List<StockAdminResponse.StockSync.StockSummary> updatedStocks = allInfos.stream()
                .map(info -> new StockAdminResponse.StockSync.StockSummary(
                        info.getSymbol(),
                        info.getName(),
                        info.getIsin(),
                        info.getMarketType(),
                        true
                ))
                .toList();

        log.info(">>> ✅ 국내 종목 정보 동기화 완료");

        // 5. 최종 결과 DTO 반환
        return new StockAdminResponse.StockSync(
                allInfos.size(),
                kospiCount,
                kosdaqCount,
                deactivatedCount,
                updatedStocks,
                "KIS 종목정보파일 동기화 완료"
        );
    }

    @Transactional
    public int processStocks(List<StockInfoDto> allInfos, int kospiCount, int kosdaqCount) {
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
                // updateInfo 호출 전 상태 확인
                boolean wasActive = stock.getIsActive();

                stock.updateInfo(info.getMarketType(), info.getSymbol(), info.getName(), info.getIsin());

                if (wasActive) {
                    log.info(">>> \uD83D\uDD35 [업데이트] 기존 종목 정보 갱신: {} ({})", info.getName(), info.getSymbol());
                } else {
                    log.info(">>> \uD83D\uDD35 [재활성화] 비활성 종목 다시 활성화: {} ({})", info.getName(), info.getSymbol());
                }
            } else {
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

        log.info("==========================================");
        log.info("📊 [종목 파일 동기화 결과]");
        log.info("📂 코스피 파일 종목 수: {} 건", kospiCount);
        log.info("📂 코스닥 파일 종목 수: {} 건", kosdaqCount);
        log.info("📦 총 종목 합계   : {} 건", allInfos.size());
        log.info("🚫 비활성화 처리 수 : {} 건", toDeactivate.size());
        log.info("==========================================");

        return toDeactivate.size();
    }
}