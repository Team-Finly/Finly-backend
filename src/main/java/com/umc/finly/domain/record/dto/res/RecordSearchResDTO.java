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

// 투자 기록 검색 응답 DTO
// 키워드 검색 결과 리스트를 반환함
@Getter
@Builder
public class RecordSearchResDTO {

    private List<SearchEntry> records; // 검색 결과 리스트
    private int totalCount;            // 총 검색 결과 수

    // 검색 결과 개별 항목
    @Getter
    @Builder
    public static class SearchEntry {
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
        public static SearchEntry from(RecordEntry entry, String symbol) {
            return SearchEntry.builder()
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
}
