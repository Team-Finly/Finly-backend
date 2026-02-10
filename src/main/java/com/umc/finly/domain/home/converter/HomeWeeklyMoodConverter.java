package com.umc.finly.domain.home.converter;

import com.umc.finly.domain.home.dto.res.HomeWeeklyMoodResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;

public class HomeWeeklyMoodConverter {

    private HomeWeeklyMoodConverter() {
    }

    // 해당 요일의 기록 목록 (없으면 빈 리스트로 반환)
    public static HomeWeeklyMoodResDTO.DayMood toEmptyDayMood(DayOfWeek day) {
        return HomeWeeklyMoodResDTO.DayMood.builder()
                .dayOfWeek(day.name().substring(0, 3))
                .hasRecord(false)
                .emotion(null)
                .build();
    } //해당 요일에 기록이 없는 경우 null, false로 설정

    public static HomeWeeklyMoodResDTO.DayMood toDayMood(    //해당 요일에 여러 개의 기록이 있는 경우 가장 마지막(createdAt 기준) 기록을 선택
            DayOfWeek day,
            List<RecordEntry> records
    ) {
        RecordEntry lastRecord = records.stream()
                .max(Comparator.comparing(RecordEntry::getCreatedAt))
                .orElseThrow();

        return HomeWeeklyMoodResDTO.DayMood.builder()
                .dayOfWeek(day.name().substring(0, 3))
                .hasRecord(true)
                .emotion(lastRecord.getEmotionCode())
                .build();
    }
}
