package com.umc.finly.domain.analysis.association.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalysisResDTO {
    private String text; // AI가 생성한 전체 텍스트
}
