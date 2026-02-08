package com.umc.finly.domain.record.dto.res;

import com.umc.finly.domain.record.entity.RecordEntry;
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

        // Entity → DTO 변환
        public static TimelineEntry from(RecordEntry entry, String symbol) {
            return TimelineEntry.builder()
                    .recordId(entry.getId())
                    .recordDate(entry.getRecordDate())
                    .recordedAt(entry.getCreatedAt())
                    .session(entry.getSession())
                    .tradeAction(entry.getTradeAction())
                    .symbol(symbol)
                    .unitPrice(entry.getUnitPrice())
                    .quantity(entry.getQuantity())
                    .emotionCode(entry.getEmotionCode())
                    .emotionIntensity(entry.getEmotionIntensity())
                    .memo(entry.getMemo())
                    .build();
        }
    }

    // 프리즘 타이틀 생성 (오늘 기록들의 감정 흐름 요약)
    // 기록 0개: 위로 메시지
    // 기록 1개: 해당 감정 강조
    // 기록 2개+: 첫 감정 → 마지막 감정 흐름 표현
    public static String generatePrismTitle(List<RecordEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return "괜찮아요. 기록이 없는 날도 있을 수 있죠.";
        }
        if (entries.size() == 1) {
            EmotionCode emotion = entries.get(0).getEmotionCode();
            if (emotion == null) { // null 체크
                return "오늘 하루도 기록을 남겼네요!";
            }
            return "{{" + emotion.getLabel() + "}}한 하루네요!";
        }
        EmotionCode first = entries.get(0).getEmotionCode();
        EmotionCode last = entries.get(entries.size() - 1).getEmotionCode();
        if (first == null || last == null) { // null 체크
            return "오늘도 열심히 기록했네요!";
        }
        // {{감정}}으로/로 조사 처리 (particle 사용)
        return "{{" + first.getLabel() + "}}하게 시작해 {{" + last.getLabel() + "}}" + last.getParticle() + " 마무리한 날이네요.";
    }
}
