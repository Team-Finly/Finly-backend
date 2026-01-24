package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;

public interface RecordService {
    RecordCreateRes createRecord(Long userId, RecordCreateReq request);
}
