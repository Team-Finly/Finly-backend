package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeMindResDTO;

public interface HomeMindService {
    HomeMindResDTO getHomeMind(Long memberId);
}
