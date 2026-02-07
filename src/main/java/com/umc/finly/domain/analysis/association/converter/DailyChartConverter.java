package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.DailyChartResDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DailyChartConverter {

    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 한국투자증권 API 응답으로 받은 일봉 주가 -> DailyDataDto 변환
    public static DailyChartResDTO.DailyDataDto toDailyDataDto(Map<String, Object> map, Integer recordCount, List<String> emotions, String mainEmotion) {

        // 날짜 처리
        LocalDate date = Optional.ofNullable(map.get("stck_bsop_date"))
                .map(String::valueOf)
                .filter(s -> s.length() == 8)
                .map(s -> {
                    try { return LocalDate.parse(s, INPUT_FORMATTER); }
                    catch (DateTimeParseException e) { return null; }
                })
                .orElse(null);

        // 종가 처리
        Integer closePrice = Optional.ofNullable(map.get("stck_clpr"))
                .map(String::valueOf)
                .map(s -> {
                    try { return Integer.parseInt(s); }
                    catch (NumberFormatException e) { return null; }
                })
                .orElse(null);

        return DailyChartResDTO.DailyDataDto.builder()
                .date(date != null ? date.format(OUTPUT_FORMATTER) : null)
                .day(date != null ? date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN) : null)
                .closePrice(closePrice)
                .recordCount(recordCount)
                .emotions(emotions)
                .mainEmotion(mainEmotion)
                .build();
    }
}