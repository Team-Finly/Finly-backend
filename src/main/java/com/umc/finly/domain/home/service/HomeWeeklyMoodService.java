package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.HomeWeeklyMoodRes;

public interface HomeWeeklyMoodService {

    HomeWeeklyMoodRes getWeeklyMood(Long memberId);
}
