package com.umc.finly.domain.analysis.emotion.service;

import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.GoldenTimeResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.ShakenKeywordsResDTO;

public interface EmotionAnalysisService {
    EmotionDistributionResDTO getEmotionDistribution(Long memberId,String symbol);  // 감정 분포 그래프
    ShakenKeywordsResDTO getShakenKeywords(Long memberId, String symbol); // 나를 흔든 키워드
    GoldenTimeResDTO getEmotionGoldenTime(Long memberId, String symbol); // 감정 골든 타임 조회
}
