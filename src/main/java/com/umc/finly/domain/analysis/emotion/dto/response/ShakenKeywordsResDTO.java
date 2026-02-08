package com.umc.finly.domain.analysis.emotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShakenKeywordsResDTO {

    private SelectedStock stock;
    private List<KeywordInfo> keywords;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedStock {
        private String symbol;             // 종목  symbol
        private String stockName;          // 주식
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordInfo {
        private int rank;                  // 키워드 순위
        private String keyword;            // 추출된 키워드
        private int count;                 // 키워드 개수
    }
}
