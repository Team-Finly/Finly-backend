package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.dto.RecordUpdateReq;
import com.umc.finly.domain.record.dto.RecordUpdateRes;

public interface RecordService {
    RecordCreateRes createRecord(Long memberId, RecordCreateReq request);
    RecordUpdateRes updateRecord(Long memberId, Long recordId, RecordUpdateReq request);
}
