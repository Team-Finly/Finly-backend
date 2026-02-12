package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.request.RecordCreateReqDTO;
import com.umc.finly.domain.record.dto.request.RecordUpdateReqDTO;
import com.umc.finly.domain.record.dto.response.DailyReportResDTO;
import com.umc.finly.domain.record.dto.response.RecentSearchResDTO;
import com.umc.finly.domain.record.dto.response.RecordCreateResDTO;
import com.umc.finly.domain.record.dto.response.RecordDetailResDTO;
import com.umc.finly.domain.record.dto.response.RecordSearchResDTO;
import com.umc.finly.domain.record.dto.response.RecordUpdateResDTO;
import com.umc.finly.domain.record.dto.response.TodayRecordResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;

import java.time.LocalDate;

// 기록(Record) 관련 비즈니스 로직 인터페이스
// 구현체: RecordServiceImpl
public interface RecordService {

    // 기록 생성 (AI 피드백 비동기 요청 포함)
    RecordCreateResDTO createRecord(Long memberId, RecordCreateReqDTO request);

    // 단일 기록 상세 조회
    RecordDetailResDTO getRecord(Long memberId, Long recordId);

    // 기록 부분 수정 (null 아닌 필드만 업데이트)
    RecordUpdateResDTO updateRecord(Long memberId, Long recordId, RecordUpdateReqDTO request);

    // 데일리 리포트 조회 (메모 AI 요약)
    DailyReportResDTO getDailyReport(Long memberId, Long recordId);

    // 특정 날짜의 기록 목록 + 프리즘 피드백 조회
    TodayRecordResDTO getTodayRecords(Long memberId, LocalDate date);

    // 키워드/감정 코드로 기록 검색
    RecordSearchResDTO searchRecords(Long memberId, String keyword, EmotionCode emotionCode);

    // 최근 검색 키워드 조회 (최대 3개)
    RecentSearchResDTO getRecentSearchKeywords(Long memberId);

    // 특정 검색 키워드 삭제
    void deleteSearchKeyword(Long memberId, String keyword);
}
