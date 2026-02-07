package com.umc.finly.domain.analysis.association.infra;

import java.util.List;
import java.util.Map;

/**
 * 한국투자증권 API - 국내주식기간별시세(일/주/월/년)[v1_국내주식-016]
 * 특정 기간 동안 (조회 시작일자 ~ 조회 종료일자) 일봉 리스트 조회
 */
public interface DailyChartApiCaller {
    List<Map<String, Object>> fetchDailyCandles(String symbol, String startDate, String endDate);
}
