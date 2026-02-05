package com.umc.finly.domain.analysis.association.controller;

import com.umc.finly.domain.analysis.association.dto.AnalysisEntryResDTO;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AssociationAnalysis", description = "통계 - 연관분석 관련 API")
public interface AssociationAnalysisApiSpecification {
    @Operation(summary = "통계 진입 사용자 상태 조회", description = """
            통계 진입한 사용자의 상태를 기록 개수에 따라 구분합니다.
            - 기록 개수 0개 (recordLevel = NONE, defaultStock = null)
            - 기록 개수 1-2개 (recordLevel = LOW, defaultStock = null)
            - 기록 개수 3개 이상 (recordLevel = HIGH, defaultStock = 가장 많이 기록한 종목 1개의 id, symbol, name)
              - 가장 많이 기록한 종목이 여러 개일 경우, 가장 최근에 기록한 종목 반환
            
            종목에 기반한 통계 관련 API를 호출할 때 API 경로에 symbol을 path variable로 넣습니다.
              - 최다 기록 종목 자동 노출의 경우 본 API 응답에서 디폴트 종목 symbol 확인
              - 사용자가 직접 종목 선택할 경우 '사용자 기록 종목 조회 API' 응답에서 symbol 확인  
            """)
    public ApiResponse<AnalysisEntryResDTO> getEntryStatus(AuthPrincipal principal);
}
