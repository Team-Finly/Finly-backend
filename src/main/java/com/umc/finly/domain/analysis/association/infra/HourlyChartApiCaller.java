package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.dto.KoreaInvestRawResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HourlyChartApiCaller {
//    // 오늘 날짜 1분봉 조회 (당일 분봉)
//    KoreaInvestRawResponse getTodayHourlyChart(String symbol, String time);
//
//    // 과거 특정 날짜 분봉 조회 (일별 분봉)
//    KoreaInvestRawResponse getPastHourlyChart(String symbol, LocalDate targetDate);

    List<Map<String, Object>> fetchHourlyChart(String symbol, String targetDate);
}
