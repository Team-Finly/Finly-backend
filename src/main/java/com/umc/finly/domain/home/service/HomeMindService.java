package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeMindRes;

public interface HomeMindService {

    HomeMindRes getHomeMind(Long memberId);
}
