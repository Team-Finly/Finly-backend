package com.umc.finly.domain.record.entity;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// 기록 엔티티
// 사용자의 활동(매수/매도/관망)과 그 때의 감정을 기록함
@Entity
@Table(name = "record_entry")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecordEntry extends CreatedUpdatedDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기록 소유자 (Member FK - ID만 저장)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 클라이언트 요청 ID (멱등성 보장용, 중복 제출 방지)
    @Column(name = "client_request_id", nullable = false, unique = true)
    private String clientRequestId;

    // 기록 날짜 (사용자가 선택한 날짜)
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    // 종목 ID (Stock FK - ID만 저장)
    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    // 매매 타입 (BUY: 매수, SELL: 매도, WATCH: 관망)
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_action", nullable = false)
    private TradeAction tradeAction;

    // 단가 (매수/매도 시 필수)
    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice;

    // 수량 (매수/매도 시 필수)
    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    // 감정 코드 (ANXIETY, GREED, CALM, CONFIDENCE, REGRET)
    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_code", nullable = false)
    private EmotionCode emotionCode;

    // 감정 강도 (1~7)
    @Column(name = "emotion_intensity", nullable = false)
    private Integer emotionIntensity;

    // 메모 (최대 200자)
    @Column(name = "memo", length = 200)
    private String memo;

    // 세션 (기록 시간대)
    @Enumerated(EnumType.STRING)
    @Column(name = "session", nullable = false)
    private Session session;
}
