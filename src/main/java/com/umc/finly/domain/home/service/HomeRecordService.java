package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeRecordsResDTO;

public interface HomeRecordService {

    HomeRecordsResDTO getRecentMyRecords(Long memberId);
}
