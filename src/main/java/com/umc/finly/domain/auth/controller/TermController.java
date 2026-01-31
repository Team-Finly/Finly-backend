package com.umc.finly.domain.auth.controller;

import com.umc.finly.domain.auth.dto.res.TermRes;
import com.umc.finly.domain.auth.service.TermQueryService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermController {

    private final TermQueryService termQueryService;

    // 약관 목록 조회
    @GetMapping
    public ApiResponse<TermRes.TermList> getTerms(){
        return ApiResponse.onSuccess(termQueryService.getTerms(), SuccessCode.OK);
    }
}
