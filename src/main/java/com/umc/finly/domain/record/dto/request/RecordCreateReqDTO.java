package com.umc.finly.domain.record.dto.request;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// 기록 생성 요청 DTO
// 클라이언트에서 새 기록 작성 시 전달하는 데이터
@Getter
@NoArgsConstructor
public class RecordCreateReqDTO {

    // 클라이언트 요청 ID (멱등성 보장용, 중복 제출 방지)
    @NotBlank(message = "clientRequestId는 필수입니다.")
    private String clientRequestId;

    // 기록 날짜 (사용자가 선택한 날짜)
    @NotNull(message = "recordDate는 필수입니다.")
    private LocalDate recordDate;

    // 종목 심볼 (예: AAPL, 005930)
    @NotBlank(message = "symbol은 필수입니다.")
    private String symbol;

    // 매매 타입 (BUY: 매수, SELL: 매도, WATCH: 관망)
    @NotNull(message = "tradeAction은 필수입니다.")
    private TradeAction tradeAction;

    // 단가 (매수/매도 시 입력, 관망 시 null 가능)
    @DecimalMin(value = "0.0", inclusive = true, message = "unitPrice는 0 이상이어야 합니다.")
    private BigDecimal unitPrice;

    // 수량 (매수/매도 시 입력, 관망 시 null 가능)
    @DecimalMin(value = "0.0", inclusive = true, message = "quantity는 0 이상이어야 합니다.")
    private BigDecimal quantity;

    // 감정 코드 (ANXIETY, GREED, CALM, CONFIDENCE, REGRET)
    @NotNull(message = "emotionCode는 필수입니다.")
    private EmotionCode emotionCode;

    // 감정 강도 (1~7, 숫자가 클수록 강함)
    @NotNull(message = "emotionIntensity는 필수입니다.")
    @Min(value = 1, message = "emotionIntensity는 1 이상이어야 합니다.")
    @Max(value = 7, message = "emotionIntensity는 7 이하이어야 합니다.")
    private Integer emotionIntensity;

    // 메모 (선택, 최대 200자)
    @Size(max = 200, message = "memo는 최대 200자까지 입력 가능합니다.")
    private String memo;
}
