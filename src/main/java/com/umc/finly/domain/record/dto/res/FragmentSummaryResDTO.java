package com.umc.finly.domain.record.dto.res;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc.finly.domain.record.enums.EmotionCode;
import lombok.*;

import java.util.List;

// 조각 모음함 요약 응답 DTO
// 전체 감정별 통계 정보 (파이 차트용)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "totalCount",
        "dominantType",
        "multipleDominant",
        "recessiveType",
        "multipleRecessive",
        "typeSummary"
})
public class FragmentSummaryResDTO {

    private int totalCount;                   // 전체 기록 개수
    private EmotionCode dominantType;         // 가장 많이 기록된 감정 타입 (동률 시 최신순)
    private boolean multipleDominant;       // 가장 많은 조각이 여러 개인지 여부
    private EmotionCode recessiveType;        // 가장 적은 조각 (동률 시 최신순)
    private boolean multipleRecessive;      // 가장 적은 조각이 여러 개인지 여부
    private List<TypeSummary> typeSummary;    // 감정 타입별 요약 정보 리스트

    // 감정 타입별 통계
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeSummary {
        private EmotionCode type;             // 감정 타입 (ANXIETY, GREED, CALM, CONFIDENCE, REGRET)
        private long count;                   // 해당 감정의 기록 개수
        private int percent;                  // 전체 대비 비율 (정수, 합계 100% 보장)
    }
}
