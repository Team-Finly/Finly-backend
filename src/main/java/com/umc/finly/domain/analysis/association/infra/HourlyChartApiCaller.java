package com.umc.finly.domain.analysis.association.infra;

import java.util.List;
import java.util.Map;

public interface HourlyChartApiCaller {

    List<Map<String, Object>> fetchHourlyChart(String symbol, String targetDate);
}
