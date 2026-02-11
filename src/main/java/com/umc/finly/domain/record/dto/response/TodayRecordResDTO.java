package com.umc.finly.domain.record.dto.response;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 특정 날짜의 기록 조회 응답 DTO
// 메인 홈 화면에 표시할 오늘 기록 요약 정보
@Getter
@Builder
public class TodayRecordResDTO {

    private LocalDate date;                      // 조회 날짜
    private PrismFeedback prismFeedback;         // 프리즘 피드백 (상단 감정 요약 문구)
    private List<TimelineEntry> timelineSummary; // 타임라인 기록 리스트
    private boolean hasRecords;                  // 기록 존재 여부
    private int recordCount;                     // 해당 날짜의 기록 개수

    // 프리즘 피드백 정보 (감정 흐름 요약)
    @Getter
    @Builder
    public static class PrismFeedback {
        private String title;              // 요약 문구
        private LocalDateTime generatedAt; // 생성 시각
    }

    // 타임라인 개별 기록
    @Getter
    @Builder
    public static class TimelineEntry {
        private Long recordId;           // 기록 ID
        private LocalDate recordDate;    // 기록 날짜
        private LocalDateTime recordedAt; // 생성 시각
        private Session session;         // 기록 시간대
        private TradeAction tradeAction; // 매매 타입
        private String symbol;           // 종목 심볼
        private BigDecimal unitPrice;    // 단가
        private BigDecimal quantity;     // 수량
        private EmotionCode emotionCode; // 감정 코드
        private Integer emotionIntensity; // 감정 강도
        private String memo;             // 메모
    }
}
