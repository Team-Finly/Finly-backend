package com.umc.finly.domain.market.stock.controller;

import com.umc.finly.domain.market.stock.dto.StockInfoResponse;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.market.stock.service.StockInfoSyncService;
import com.umc.finly.domain.market.stock.service.StockLogoUpdateService;
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

    // KIS 종목정보 동기화
    @PostMapping("/sync")
    public ApiResponse<String> syncStockInfo() {
        stockInfoSyncService.syncDomesticStocks();
        return ApiResponse.onSuccess("KIS 종목정보 동기화가 완료되었습니다.", SuccessCode.OK);
    }

    // TradingView 로고 업데이트
    @PostMapping("/logo")
    public ApiResponse<String> updateLogos() { //
        stockLogoUpdateService.updateMissingLogos();
        return ApiResponse.onSuccess("TradingView 로고 URL 업데이트가 완료되었습니다.", SuccessCode.OK);
    }

    // 1,2 모두 실행
    @PostMapping("/all")
    public ApiResponse<String> syncStockInfoAndUpdateLogos() {
        stockInfoSyncService.syncDomesticStocks();
        stockLogoUpdateService.updateMissingLogos();
        return ApiResponse.onSuccess("종목 정보 동기화 + 로고 URL 업데이트가 완료되었습니다.", SuccessCode.OK);
    }

    // DB 모든 종목 리스트 조회
    @GetMapping("/stocks")
    public ApiResponse<List<StockInfoResponse>> getAllStocks() {
        List<StockInfoResponse> result = stockRepository.findAll().stream()
                .map(StockInfoResponse::from)
                .toList();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 스케줄러 실행
}
