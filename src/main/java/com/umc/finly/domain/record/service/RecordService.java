package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.req.RecordCreateReqDTO;
import com.umc.finly.domain.record.dto.req.RecordUpdateReqDTO;
import com.umc.finly.domain.record.dto.res.DailyReportResDTO;
import com.umc.finly.domain.record.dto.res.RecentSearchResDTO;
import com.umc.finly.domain.record.dto.res.RecordCreateResDTO;
import com.umc.finly.domain.record.dto.res.RecordDetailResDTO;
import com.umc.finly.domain.record.dto.res.RecordSearchResDTO;
import com.umc.finly.domain.record.dto.res.RecordUpdateResDTO;
import com.umc.finly.domain.record.dto.res.TodayRecordResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;

import java.time.LocalDate;

public interface RecordService {
    RecordCreateResDTO createRecord(Long memberId, RecordCreateReqDTO request);
    RecordDetailResDTO getRecord(Long memberId, Long recordId);
    RecordUpdateResDTO updateRecord(Long memberId, Long recordId, RecordUpdateReqDTO request);
    DailyReportResDTO getDailyReport(Long memberId, Long recordId);
    TodayRecordResDTO getTodayRecords(Long memberId, LocalDate date);
    RecordSearchResDTO searchRecords(Long memberId, String keyword, EmotionCode emotionCode);
    RecentSearchResDTO getRecentSearchKeywords(Long memberId);
}
