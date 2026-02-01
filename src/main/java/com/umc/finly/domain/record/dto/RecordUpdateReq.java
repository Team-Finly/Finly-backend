package com.umc.finly.domain.record.dto;

import com.umc.finly.domain.record.entity.EmotionCode;
import com.umc.finly.domain.record.entity.TradeAction;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RecordUpdateReq {

    private LocalDate recordDate;

    private Long stockId;

    private TradeAction tradeAction;

    @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice는 0보다 커야 합니다.")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "quantity는 0보다 커야 합니다.")
    private BigDecimal quantity;

    private EmotionCode emotionCode;

    @Min(value = 1, message = "emotionIntensity는 1 이상이어야 합니다.")
    @Max(value = 7, message = "emotionIntensity는 7 이하이어야 합니다.")
    private Integer emotionIntensity;

    @Size(max = 200, message = "memo는 최대 200자까지 입력 가능합니다.")
    private String memo;
}
