package com.umc.finly.domain.analysis.emotion.converter;

import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.ShakenKeywordsResDTO;
import com.umc.finly.domain.market.stock.entity.Stock;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EmotionAnalysisConverter {

    // 감정 분포 그래프 응답 DTO 변환
    public EmotionDistributionResDTO toEmotionDistributionResDTO(
            long totalCount,
            EmotionDistributionResDTO.SelectedStock stock,
            List<EmotionDistributionResDTO.TypeSummary> summaries) {
        return EmotionDistributionResDTO.builder()
                .totalCount((int) totalCount)
                .stock(stock)
                .typeSummary(summaries)
                .build();
    }

    // 나를 흔든 키워드 응답 DTO로 변환
    public static ShakenKeywordsResDTO toShakenKeywordsResDTO(
            Stock stock,
            List<Map.Entry<String, Integer>> rankedKeywords
    ) {
        List<ShakenKeywordsResDTO.KeywordInfo> items = new ArrayList<>();

        for (int i = 0; i < rankedKeywords.size(); i++) {
            Map.Entry<String, Integer> e = rankedKeywords.get(i);

            items.add(ShakenKeywordsResDTO.KeywordInfo.builder()
                    .rank(i + 1)
                    .keyword(e.getKey())
                    .count(e.getValue())
                    .build());
        }

        return ShakenKeywordsResDTO.builder()
                .stock(ShakenKeywordsResDTO.SelectedStock.builder()
                        .symbol(stock.getSymbol())
                        .stockName(stock.getName())
                        .build())
                .keywords(items)
                .build();
    }
}