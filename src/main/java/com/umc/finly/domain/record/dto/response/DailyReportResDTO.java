package com.umc.finly.domain.record.dto.response;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 일일 리포트 응답 DTO
// 특정 기록에 대한 AI 피드백 내용 포함
@Getter
@Builder
public class DailyReportResDTO {

    private Long recordId;           // 기록 ID
    private LocalDate recordDate;    // 기록 날짜
    private LocalDateTime recordedAt; // 생성 시각
    private Session session;         // 기록 시간대
    private TradeAction tradeAction; // 매매 타입
    private BigDecimal unitPrice;    // 단가
    private BigDecimal quantity;     // 수량
    private EmotionCode emotionCode; // 감정 코드
    private String name;             // 종목명 (symbol 아닌 name)
    private String content;          // AI 피드백 내용
}
