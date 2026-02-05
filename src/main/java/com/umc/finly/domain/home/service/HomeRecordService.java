package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.HomeRecordsRes;

public interface HomeRecordService {

    HomeRecordsRes getRecentMyRecords(Long memberId);
}
