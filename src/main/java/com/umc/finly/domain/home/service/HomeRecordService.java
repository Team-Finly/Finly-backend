package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.response.HomeRecordsResDTO;

public interface HomeRecordService {

    HomeRecordsResDTO getRecentMyRecords(Long memberId);
}
