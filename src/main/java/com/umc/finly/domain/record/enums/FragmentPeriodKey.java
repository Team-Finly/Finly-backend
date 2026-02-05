package com.umc.finly.domain.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FragmentPeriodKey {
    ALL(0),      // 전체
    MONTH_1(1),  // 1개월
    MONTH_3(3),  // 3개월
    MONTH_6(6),  // 6개월
    YEAR_1(12);  // 1년

    private final int months;
}

