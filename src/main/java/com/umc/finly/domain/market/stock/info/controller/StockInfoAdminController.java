package com.umc.finly.domain.market.stock.info.controller;

import com.umc.finly.domain.market.stock.info.dto.response.StockAdminResDTO;
import com.umc.finly.domain.market.stock.info.dto.response.StockInfoResDTO;
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
    public ApiResponse<StockAdminResDTO.StockSync> syncStockInfo() {
        StockAdminResDTO.StockSync result = stockInfoSyncService.syncDomesticStocks();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // TradingView 로고 업데이트
    @PostMapping("/logo")
    public ApiResponse<StockAdminResDTO.LogoUpdate> updateLogos() {
        StockAdminResDTO.LogoUpdate result = stockLogoUpdateService.updateMissingLogos();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 1,2 모두 실행
    @PostMapping("/all")
    public ApiResponse<StockAdminResDTO.TotalSync> syncStockInfoAndUpdateLogos() {
        StockAdminResDTO.StockSync syncResult = stockInfoSyncService.syncDomesticStocks();
        StockAdminResDTO.LogoUpdate logoResult = stockLogoUpdateService.updateMissingLogos();

        StockAdminResDTO.TotalSync totalResult = new StockAdminResDTO.TotalSync(syncResult, logoResult);
        return ApiResponse.onSuccess(totalResult, SuccessCode.OK);
    }

    // 스케줄러 실행
    @PostMapping("/scheduler")
    public ApiResponse<StockAdminResDTO.TotalSync> triggerScheduler() {
        StockAdminResDTO.TotalSync result = stockInfoScheduler.runDailyJob();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // DB 모든 종목 리스트 조회
    @GetMapping("/stocks")
    public ApiResponse<List<StockInfoResDTO>> getAllStocks() {
        List<StockInfoResDTO> result = stockRepository.findAll().stream()
                .map(StockInfoResDTO::from)
                .toList();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
