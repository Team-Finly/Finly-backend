package com.umc.finly.domain.record.dto.response;

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
    private long totalRecords;           // 조회 범위 내 전체 기록 수
    private List<Day> days;              // 기록이 존재하는 날짜 리스트

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Range{
        private LocalDate from;          // 시작 날짜
        private LocalDate to;            // 종료 날짜
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Day{
        private LocalDate date;          // 기록 날짜
        private long totalCount;         // 해당 날짜의 총 기록 갯수
        private List<TypeCount> byType;  // 감정별 기록 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeCount{
        private EmotionCode type;        // 감정 타입
        private long count;              // 해당 감정 기록 수
    }
}
