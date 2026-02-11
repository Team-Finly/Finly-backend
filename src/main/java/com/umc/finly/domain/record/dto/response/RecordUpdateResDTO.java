package com.umc.finly.domain.record.dto.response;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 투자 기록 수정 응답 DTO
// 수정 완료 후 변경된 기록 정보를 반환함
@Getter
@Builder
public class RecordUpdateResDTO {

    private Long recordId;           // 기록 ID
    private LocalDate recordDate;    // 기록 날짜
    private LocalDateTime updatedAt; // 수정 시각
    private Session session;         // 기록 시간대
    private TradeAction tradeAction; // 매매 타입
    private String symbol;           // 종목 심볼
    private BigDecimal unitPrice;    // 단가
    private BigDecimal quantity;     // 수량
    private EmotionCode emotionCode; // 감정 코드
    private Integer emotionIntensity; // 감정 강도
    private String memo;             // 메모

    // Entity → DTO 변환
    public static RecordUpdateResDTO from(RecordEntry entry, Stock stock) {
        return RecordUpdateResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .updatedAt(entry.getUpdatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(stock.getSymbol())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }
}
