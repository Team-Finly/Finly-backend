package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.response.FragmentListResDTO;
import com.umc.finly.domain.record.dto.response.FragmentSummaryResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;

public interface FragmentService {
    FragmentSummaryResDTO getFragmentSummary(Long memberId); // 조각 모음함 요약 조회
    FragmentListResDTO getFragmentList(Long memberId, EmotionCode boxType, FragmentPeriodKey periodKey); // 조각 모음함 리스트 조회
}
