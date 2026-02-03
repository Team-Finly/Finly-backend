package com.umc.finly.domain.analysis.association.controller;

import com.umc.finly.domain.analysis.association.dto.StockRecordRes;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis")
public class AssociationAnalysisController {


    @GetMapping("/record/stocks") //사용자 종목 기록 조회 API
    public ApiResponse<StockRecordRes> getRecordStocks() {



        return ApiResponse.onSuccess(result, SuccessCode.OK);

    }

}
