package com.umc.finly.domain.record.dto.req;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RecordCreateReqDTO {

    @NotBlank(message = "clientRequestId는 필수입니다.")
    private String clientRequestId;

    @NotNull(message = "recordDate는 필수입니다.")
    private LocalDate recordDate;

    @NotBlank(message = "symbol은 필수입니다.")
    private String symbol;

    @NotNull(message = "tradeAction은 필수입니다.")
    private TradeAction tradeAction;

    @DecimalMin(value = "0.0", inclusive = true, message = "unitPrice는 0 이상이어야 합니다.")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "quantity는 0 이상이어야 합니다.")
    private BigDecimal quantity;

    @NotNull(message = "emotionCode는 필수입니다.")
    private EmotionCode emotionCode;

    @NotNull(message = "emotionIntensity는 필수입니다.")
    @Min(value = 1, message = "emotionIntensity는 1 이상이어야 합니다.")
    @Max(value = 7, message = "emotionIntensity는 7 이하이어야 합니다.")
    private Integer emotionIntensity;

    @Size(max = 200, message = "memo는 최대 200자까지 입력 가능합니다.")
    private String memo;
}
