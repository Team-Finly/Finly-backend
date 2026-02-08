package com.umc.finly.domain.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 조각 모음함 기간 필터 enum
// 조각 리스트 조회 시 기간 필터링에 사용
// months 값으로 from 날짜 계산 (months개월)
@Getter
@RequiredArgsConstructor
public enum FragmentPeriodKey {
    ALL(0),      // 전체 - 기간 제한 없음
    MONTH_1(1),  // 1개월 - 최근 1개월
    MONTH_3(3),  // 3개월 - 최근 3개월
    MONTH_6(6),  // 6개월 - 최근 6개월
    YEAR_1(12);  // 1년 - 최근 12개월

    private final int months; // 개월 수 (0이면 전체)
}

