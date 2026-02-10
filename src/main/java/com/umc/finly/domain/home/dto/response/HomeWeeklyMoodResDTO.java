package com.umc.finly.domain.home.dto.response;

import com.umc.finly.domain.record.enums.EmotionCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeWeeklyMoodResDTO {

    private List<DayMood> days;

    @Getter
    @Builder
    public static class DayMood {
        private String dayOfWeek;      // 요일
        private boolean hasRecord;     // 기록 존재 여부 없으면 null로 출력하기
        private EmotionCode emotion;   // 대표 감정
    }
}
