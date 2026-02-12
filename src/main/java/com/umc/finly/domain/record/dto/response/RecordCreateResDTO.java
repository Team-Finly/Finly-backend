package com.umc.finly.domain.record.dto.response;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 투자 기록 생성 응답 DTO
// 기록 생성 완료 후 클라이언트에 반환하는 데이터
@Getter
@Builder
public class RecordCreateResDTO {

    private Long recordId;           // 생성된 기록 ID
    private LocalDate recordDate;    // 기록 날짜
    private LocalDateTime recordedAt; // 생성 시각
    private Session session;         // 기록 시간대 (PRE_MARKET, MORNING, AFTERNOON, POST_MARKET)
    private TradeAction tradeAction; // 매매 타입
    private String symbol;           // 종목 심볼
    private EmotionCode emotionCode; // 감정 코드
    private Integer emotionIntensity; // 감정 강도 (1~7)
    private String memo;             // 메모
    private FeedbackInfo feedback;   // AI 피드백 정보 (생성 직후에는 PENDING 상태)

    // AI 피드백 요약 정보
    @Getter
    @Builder
    public static class FeedbackInfo {
        private Long feedbackId;     // 피드백 ID
        private String status;       // 피드백 상태 (PENDING, GENERATING, COMPLETED, FAILED)
    }
}
