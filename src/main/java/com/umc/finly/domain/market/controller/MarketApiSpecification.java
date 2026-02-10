package com.umc.finly.domain.market.controller;

import com.umc.finly.domain.market.dto.response.MarketIndexResDTO;
import com.umc.finly.domain.market.dto.response.MarketInsightResDTO;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface MarketApiSpecification {

    @Operation(
            summary = "시장 지수 조회",
            description = """
                    현재 시장 지표 정보를 조회합니다.
                    
                    - KOSPI / KOSDAQ 지수
                    - 공포·탐욕 지수 및 상태
                    - 마지막 업데이트 시각
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "시장 지수 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "시장 지수 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "COMMON200",
                                              "message": "요청에 성공했습니다.",
                                              "result": {
                                                "kospi": 5088.79,
                                                "kosdaq": 1078.5,
                                                "fearGreed": 33,
                                                "fearGreedStatus": "FEAR",
                                                "updatedAt": "2026-02-06T15:03:27"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<MarketIndexResDTO> getMarketIndex();

    @Operation(
            summary = "실시간 시장 인사이트 조회",
            description = """
                    특정 종목을 중심으로 한 실시간 시장 인사이트를 조회합니다.
                    
                    - 현재 시장 참여자의 감정 상태
                    - 매수/매도 우세 비율
                    - 신뢰도 레벨
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "실시간 시장 인사이트 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "시장 인사이트 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "COMMON200",
                                              "message": "요청에 성공했습니다.",
                                              "result": {
                                                "stockName": "삼성전자",
                                                "message": "지금 삼성전자 주주들은 욕심이 커진 상태예요",
                                                "dominantEmotion": "GREED",
                                                "buySellRatio": "BUY_DOMINANT",
                                                "confidenceLevel": "LOW"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<MarketInsightResDTO> getInsightMarketIndex();
}
