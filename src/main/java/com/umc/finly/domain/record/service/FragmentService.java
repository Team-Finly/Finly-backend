package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.res.FragmentListResDTO;
import com.umc.finly.domain.record.dto.res.FragmentSummaryResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;

// 조각 모음함(Fragment) 관련 비즈니스 로직 인터페이스
// 감정별 기록 통계 및 리스트 조회를 담당함
// 구현체: FragmentServiceImpl
public interface FragmentService {

    // 조각 모음함 요약 조회 (전체 개수, 가장 많은 감정, 감정별 비율)
    FragmentSummaryResDTO getFragmentSummary(Long memberId);

    // 조각 모음함 리스트 조회 (감정/기간 필터링 가능)
    FragmentListResDTO getFragmentList(Long memberId, EmotionCode boxType, FragmentPeriodKey periodKey);
}
