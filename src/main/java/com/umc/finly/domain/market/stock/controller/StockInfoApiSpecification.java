package com.umc.finly.domain.market.stock.controller;

import com.umc.finly.domain.market.stock.dto.StockAdminResponse;
import com.umc.finly.domain.market.stock.dto.StockInfoResponse;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "StockInfo", description = "종목 정보 관련 API (개발/운영/테스트용)")
public interface StockInfoApiSpecification {
    @Operation(summary = "종목 정보 동기화", description = "KIS 코스피, 코스닥 종목 정보 파일을 다운로드 및 파싱한 후 DB에 저장합니다.")
    public ApiResponse<StockAdminResponse.StockSync> syncStockInfo();

//    @Operation(summary = "로고 URL 업데이트", description = "TradingView에서 로고 이미지 URL 찾아 DB에 저장합니다.")
//    public ApiResponse<StockAdminResponse.LogoUpdate> updateLogos();
//
//    @Operation(summary = "종목 정보 완전 저장", description = "종목 정보 저장과 로고 이미지 URL 저장을 모두 실행합니다.")
//    public ApiResponse<StockAdminResponse.TotalSync> syncStockInfoAndUpdateLogos();
//
//    @Operation(summary = "스케줄러 실행", description = "종목 정보 저장과 로고 이미지 URL 저장을 모두 실행하는 스케줄러를 실행합니다.")
//    public ApiResponse<StockAdminResponse.TotalSync> triggerScheduler();

    @Operation(summary = "저장된 종목 확인", description = "DB에 저장된 모든 종목 정보 리스트를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ApiResponse<List<StockInfoResponse>> getAllStocks();
}
