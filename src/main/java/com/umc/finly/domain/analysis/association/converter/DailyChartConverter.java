package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.response.DailyChartResDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyChartConverter {

    // 한국투자증권 API 응답으로 받은 일봉 주가 -> DailyDataDto 변환
    public static DailyChartResDTO.DailyDataDto toDailyDataDto(Map<String, Object> map, LocalDate date, Integer recordCount, List<String> emotions, String mainEmotion) {

        // 종가 처리
        Integer closePrice = Optional.ofNullable(map.get("stck_clpr"))
                .map(String::valueOf)
                .map(s -> {
                    try { return Integer.parseInt(s); }
                    catch (NumberFormatException e) { return null; }
                })
                .orElse(null);

        return DailyChartResDTO.DailyDataDto.builder()
                .date(date.toString())
                .day(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                .closePrice(closePrice)
                .recordCount(recordCount)
                .emotions(emotions)
                .mainEmotion(mainEmotion)
                .build();
    }
}