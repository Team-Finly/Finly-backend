package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.response.HourlyChartResDTO;

import java.time.LocalDate;

public interface HourlyChartService {

    HourlyChartResDTO getHourlyChart(Long memberId, String symbol, LocalDate targetDate);
}
