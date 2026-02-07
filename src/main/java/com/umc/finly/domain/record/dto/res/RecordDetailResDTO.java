package com.umc.finly.domain.record.dto.res;

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

// 투자 기록 상세 조회 응답 DTO
// 단일 기록의 모든 정보를 포함함
@Getter
@Builder
public class RecordDetailResDTO {

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
    public static RecordDetailResDTO from(RecordEntry entry, Stock stock) {
        return RecordDetailResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
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
