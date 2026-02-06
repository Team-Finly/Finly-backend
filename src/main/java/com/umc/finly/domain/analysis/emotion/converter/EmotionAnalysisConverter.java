package com.umc.finly.domain.analysis.emotion.converter;

import com.umc.finly.domain.analysis.emotion.DTO.response.EmotionDistributionResDTO;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
