package com.umc.finly.domain.record.dto.request;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// 기록 수정 요청 DTO
// 기존 기록의 부분 수정을 위한 데이터 (모든 필드 optional)
@Getter
@NoArgsConstructor
public class RecordUpdateReqDTO {

    // 기록 날짜 변경 (null이면 변경 안함)
    private LocalDate recordDate;

    // 종목 심볼 변경 (null이면 변경 안함)
    private String symbol;

    // 매매 타입 변경 (null이면 변경 안함)
    private TradeAction tradeAction;

    // 단가 변경 (0 이상)
    @DecimalMin(value = "0.0", inclusive = true, message = "unitPrice는 0 이상이어야 합니다.")
    private BigDecimal unitPrice;

    // 수량 변경 (0 이상)
    @DecimalMin(value = "0.0", inclusive = true, message = "quantity는 0 이상이어야 합니다.")
    private BigDecimal quantity;

    // 감정 코드 변경 (null이면 변경 안함)
    private EmotionCode emotionCode;

    // 감정 강도 변경 (1~7)
    @Min(value = 1, message = "emotionIntensity는 1 이상이어야 합니다.")
    @Max(value = 7, message = "emotionIntensity는 7 이하이어야 합니다.")
    private Integer emotionIntensity;

    // 메모 변경 (최대 200자)
    @Size(max = 200, message = "memo는 최대 200자까지 입력 가능합니다.")
    private String memo;
}
