package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.response.HomeWeeklyMoodResDTO;

public interface HomeWeeklyMoodService {

    HomeWeeklyMoodResDTO getWeeklyMood(Long memberId);
}
