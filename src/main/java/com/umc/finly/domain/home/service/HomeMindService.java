package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeMindDetailResDTO;
import com.umc.finly.domain.home.dto.res.HomeMindResDTO;

public interface HomeMindService {
    HomeMindResDTO getHomeMind(Long memberId); // 요약 조회
    HomeMindDetailResDTO getHomeMindDetail(Long memberId); // 상세 조회
}
