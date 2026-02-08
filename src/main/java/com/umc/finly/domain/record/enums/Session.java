package com.umc.finly.domain.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

// 기록 시간대 enum
// 투자 기록 생성 시 자동으로 현재 시간 기준 세션 결정
// 한국 주식시장 운영 시간 기준으로 구분함
@Getter
@RequiredArgsConstructor
public enum Session {
    PRE_MARKET("장 전"),   // 00:00 ~ 09:00 (장전 브리핑 - 예측의 시간)
    MORNING("오전"),      // 09:00 ~ 12:00 (오전 - 변동성의 시간)
    AFTERNOON("오후"),    // 12:00 ~ 15:30 (오후 - 이성의 시간)
    POST_MARKET("장 마감");  // 15:30 ~ 00:00 (장후 복기 - 정리의 시간)

    // 시간대 경계값
    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 0);   // 장 시작
    private static final LocalTime NOON = LocalTime.of(12, 0);          // 정오
    private static final LocalTime MARKET_CLOSE = LocalTime.of(15, 30); // 장 마감

    // 현재 시간으로부터 세션 결정
    public static Session fromTime(LocalTime time) {
        if (time.isBefore(MARKET_OPEN)) {
            return PRE_MARKET;
        } else if (time.isBefore(NOON)) {
            return MORNING;
        } else if (time.isBefore(MARKET_CLOSE)) {
            return AFTERNOON;
        } else {
            return POST_MARKET;
        }
    }

    private final String label;

    // 골든타임용
    public String getGoldenTimeName() {
        return switch (this) {
            case PRE_MARKET -> "장 전";
            case POST_MARKET -> "장 마감";
            case MORNING -> "오전 장";
            case AFTERNOON -> "오후 장";
        };
    }

    // 세션 분포 그래프용
    public String getSessionName() {
        return label;
    }
}
