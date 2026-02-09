package com.umc.finly.domain.record.dto.res;

import com.umc.finly.domain.record.enums.EmotionCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FragmentCalendarResDTO {

    private String yearMonth;            // 조회 년월 (yyyy-mm)
    private Range range;                 // 조회 범위
    private long totalRecords;           // 기록이 존재하는 날짜 리스트
    private List<Day> days;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Range{
        private LocalDate from;
        private LocalDate to;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Day{
        private LocalDate date;
        private long totalCount;
        private List<TypeCount> byType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeCount{
        private EmotionCode type;
        private long count;
    }
}
