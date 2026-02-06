package com.umc.finly.domain.analysis.emotion.service;

import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.ShakenKeywordsResDTO;

public interface EmotionAnalysisService {
    EmotionDistributionResDTO getEmotionDistribution(Long memberId,String symbol);  // 감정 분포 그래프
    ShakenKeywordsResDTO getShakenKeywords(Long memberId, String symbol); // 나를 흔든 키워드
}
