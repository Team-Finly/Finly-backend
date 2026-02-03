package com.umc.finly.domain.record.entity;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "client_request_id", nullable = false, unique = true)
    private String clientRequestId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_action", nullable = false)
    private TradeAction tradeAction;

    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_code", nullable = false)
    private EmotionCode emotionCode;

    @Column(name = "emotion_intensity", nullable = false)
    private Integer emotionIntensity;

    @Column(name = "memo", length = 200)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "session", nullable = false)
    private Session session;
}
