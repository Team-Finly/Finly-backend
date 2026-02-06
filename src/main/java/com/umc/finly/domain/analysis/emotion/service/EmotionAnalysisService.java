package com.umc.finly.domain.analysis.emotion.service;

import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;

public interface EmotionAnalysisService {
    EmotionDistributionResDTO getEmotionDistribution(Long memberId,String symbol);  // 감정 분포 그래프
}
