package com.umc.finly.domain.analysis.association.controller;

import com.umc.finly.domain.analysis.association.dto.StockRecordRes;
import com.umc.finly.domain.analysis.association.service.AssociationAnalysisService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis")
public class AssociationAnalysisController {

    private final AssociationAnalysisService associationAnalysisService;

    @GetMapping("/record/stocks") //사용자 종목 기록 조회 API
    public ApiResponse<List<StockRecordRes>> getRecordStocks(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                associationAnalysisService.getRecordedStocks(memberId),
                SuccessCode.OK
        );
    }

}
