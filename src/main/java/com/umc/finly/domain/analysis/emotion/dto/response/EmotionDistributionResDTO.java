package com.umc.finly.domain.analysis.emotion.dto.response;

import com.umc.finly.domain.record.enums.EmotionCode;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionDistributionResDTO {

    private int totalCount;                   // 전체 기록 개수
    private SelectedStock stock;
    private List<TypeSummary> typeSummary;    // 감정 타입 별 요약 정보 리스트


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedStock{
        private String symbol;
        private String stockName;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeSummary {
        private EmotionCode type;             // 감정 타입 : ANXIETY, GREED, CALM, CONFIDENCE, REGRET
        private long count;                   // 해당 감정의 기록 개수
        private int percent;                  // 전체 대비 비율 (정수, 합 100%)
    }
}