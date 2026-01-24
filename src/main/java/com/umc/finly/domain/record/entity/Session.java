package com.umc.finly.domain.record.entity;

import java.time.LocalTime;

public enum Session {
    MORNING,    // 06:00 ~ 11:59
    AFTERNOON,  // 12:00 ~ 17:59
    EVENING;    // 18:00 ~ 05:59

    public static Session fromTime(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 12) {
            return MORNING;
        } else if (hour >= 12 && hour < 18) {
            return AFTERNOON;
        } else {
            return EVENING;
        }
    }
}
