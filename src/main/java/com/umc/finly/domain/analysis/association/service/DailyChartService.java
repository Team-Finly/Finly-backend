package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.response.DailyChartResDTO;

public interface DailyChartService {
    // 특정 종목 최근 7영업일의 주가 데이터와 사용자 기록을 결합하여 날짜별로 반환
    DailyChartResDTO getDailyChart(Long memberId, String symbol);
}
