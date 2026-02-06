package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeWeeklyMoodRes;

public interface HomeWeeklyMoodService {

    HomeWeeklyMoodRes getWeeklyMood(Long memberId);
}
