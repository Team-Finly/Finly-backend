package com.umc.finly.domain.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public enum Session {
    PRE_MARKET("장 전"),   // 00:00 ~ 09:00 (장전 브리핑 - 예측의 시간)
    MORNING("오전"),      // 09:00 ~ 12:00 (오전 - 변동성의 시간)
    AFTERNOON("오후"),    // 12:00 ~ 15:30 (오후 - 이성의 시간)
    POST_MARKET("장 마감");  // 15:30 ~ 00:00 (장후 복기 - 정리의 시간)

    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(15, 30);

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
