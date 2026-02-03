package com.umc.finly.domain.market.stock.info.controller;

import com.umc.finly.domain.market.stock.info.dto.StockAdminResponse;
import com.umc.finly.domain.market.stock.info.dto.StockInfoResponse;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.market.stock.info.service.StockInfoScheduler;
import com.umc.finly.domain.market.stock.info.service.StockInfoSyncService;
import com.umc.finly.domain.market.stock.info.service.StockLogoUpdateService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 프론트용이 아닌, 개발/운영/테스트용 내부 관리 API.
 */
@RestController
@RequestMapping("/api/internal/stock-info")
@RequiredArgsConstructor
public class StockInfoAdminController implements StockInfoApiSpecification {

    private final StockInfoSyncService stockInfoSyncService;
    private final StockLogoUpdateService stockLogoUpdateService;
    private final StockRepository stockRepository;
    private final StockInfoScheduler stockInfoScheduler;

    // KIS 종목정보 동기화
    @PostMapping("/sync")
    public ApiResponse<StockAdminResponse.StockSync> syncStockInfo() {
        StockAdminResponse.StockSync result = stockInfoSyncService.syncDomesticStocks();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // TradingView 로고 업데이트
    @PostMapping("/logo")
    public ApiResponse<StockAdminResponse.LogoUpdate> updateLogos() {
        StockAdminResponse.LogoUpdate result = stockLogoUpdateService.updateMissingLogos();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 1,2 모두 실행
    @PostMapping("/all")
    public ApiResponse<StockAdminResponse.TotalSync> syncStockInfoAndUpdateLogos() {
        StockAdminResponse.StockSync syncResult = stockInfoSyncService.syncDomesticStocks();
        StockAdminResponse.LogoUpdate logoResult = stockLogoUpdateService.updateMissingLogos();

        StockAdminResponse.TotalSync totalResult = new StockAdminResponse.TotalSync(syncResult, logoResult);
        return ApiResponse.onSuccess(totalResult, SuccessCode.OK);
    }

    // 스케줄러 실행
    @PostMapping("/scheduler")
    public ApiResponse<StockAdminResponse.TotalSync> triggerScheduler() {
        StockAdminResponse.TotalSync result = stockInfoScheduler.runDailyJob();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // DB 모든 종목 리스트 조회
    @GetMapping("/stocks")
    public ApiResponse<List<StockInfoResponse>> getAllStocks() {
        List<StockInfoResponse> result = stockRepository.findAll().stream()
                .map(StockInfoResponse::from)
                .toList();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
