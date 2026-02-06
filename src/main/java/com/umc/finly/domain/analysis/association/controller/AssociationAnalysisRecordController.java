package com.umc.finly.domain.analysis.association.controller;


import com.umc.finly.domain.analysis.association.dto.StockRecordResDTO;
import com.umc.finly.domain.analysis.association.service.AssociationAnalysisService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "AssociationAnalysisRecord", description = "사용자 기록 기반 종목 조회 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis")
public class AssociationAnalysisRecordController {

    private final AssociationAnalysisService associationAnalysisService;
    @Operation(
            summary = "사용자가 기록한 종목 목록 조회",
            description = """
                    사용자가 매매 기록을 남긴 적이 있는 종목 목록을 조회합니다.

                    - 동일 종목을 여러 번 기록했더라도 종목당 1회만 반환됩니다.
                    - 사용자 인증 정보(JWT)를 기반으로 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자가 기록한 종목 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "ANALYSIS200",
                                              "message": "사용자가 기록한 종목 목록을 정상적으로 조회했습니다.",
                                              "result": [
                                                {
                                                  "stockId": 123,
                                                  "stockCode": "005930",
                                                  "stockName": "삼성전자"
                                                },
                                                {
                                                  "stockId": 124,
                                                  "stockCode": "000660",
                                                  "stockName": "SK하이닉스"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/record/stocks") //사용자 종목 기록 조회 API
    public ApiResponse<List<StockRecordResDTO>> getRecordStocks(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        return ApiResponse.onSuccess(
                associationAnalysisService.getRecordedStocks(principal.getMemberId()),
                SuccessCode.OK
        );
    }

}