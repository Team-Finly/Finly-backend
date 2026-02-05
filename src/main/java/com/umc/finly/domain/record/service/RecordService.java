package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.DailyReportRes;
import com.umc.finly.domain.record.dto.RecentSearchRes;
import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.dto.RecordDetailRes;
import com.umc.finly.domain.record.dto.RecordSearchRes;
import com.umc.finly.domain.record.dto.RecordUpdateReq;
import com.umc.finly.domain.record.dto.RecordUpdateRes;
import com.umc.finly.domain.record.dto.TodayRecordRes;
import com.umc.finly.domain.record.enums.EmotionCode;

import java.time.LocalDate;

public interface RecordService {
    RecordCreateRes createRecord(Long memberId, RecordCreateReq request);
    RecordDetailRes getRecord(Long memberId, Long recordId);
    RecordUpdateRes updateRecord(Long memberId, Long recordId, RecordUpdateReq request);
    DailyReportRes getDailyReport(Long memberId, Long recordId);
    TodayRecordRes getTodayRecords(Long memberId, LocalDate date);
    RecordSearchRes searchRecords(Long memberId, String keyword, EmotionCode emotionCode);
    RecentSearchRes getRecentSearchKeywords(Long memberId);
}
