package com.umc.finly.domain.record.converter;

import com.umc.finly.domain.record.dto.response.FragmentSummaryResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FragmentConverter {

    public FragmentSummaryResDTO toFragmentSummaryRes(
            long totalCount,
            EmotionCode dominantType,
            List<FragmentSummaryResDTO.TypeSummary> summaries) {
        return FragmentSummaryResDTO.builder()
                .totalCount((int) totalCount)
                .dominantType(dominantType)
                .typeSummary(summaries)
                .build();
    }
}