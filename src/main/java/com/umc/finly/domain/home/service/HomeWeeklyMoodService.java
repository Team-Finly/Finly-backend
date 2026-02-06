package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeWeeklyMoodResDTO;

public interface HomeWeeklyMoodService {

    HomeWeeklyMoodResDTO getWeeklyMood(Long memberId);
}
