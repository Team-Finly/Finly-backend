package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.DailyChartResDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyChartConverter {

    // 한국투자증권 API 응답으로 받은 일봉 주가 -> DailyDataDto 변환
    public static DailyChartResDTO.DailyDataDto toDailyDataDto(Map<String, Object> map, Integer recordCount, List<String> emotions, String mainEmotion) {
        LocalDate date = LocalDate.parse(String.valueOf(map.get("stck_bsop_date")), DateTimeFormatter.ofPattern("yyyyMMdd"));

        return DailyChartResDTO.DailyDataDto.builder()
                .date(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .day(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                .closePrice(Integer.parseInt(String.valueOf(map.get("stck_clpr"))))
                .recordCount(recordCount)
                .emotions(emotions)
                .mainEmotion(mainEmotion)
                .build();
    }
}
