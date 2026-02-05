package com.umc.finly.domain.record.dto.response;

import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;
import com.umc.finly.domain.record.enums.TradeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FragmentListResDTO {

    private BoxInfo box;                      // 현재 선택된 조각 모음함 정보
    private PeriodInfo period;                // 적용된 기간 정보
    private Summary summary;                  // 상단 요약 (개수/타이틀)
    private List<Fragment> fragments;         // 조각 리스트

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxInfo {
        private EmotionCode boxType;          // 조각함 타입
        private String boxTypeName;           // 화면 표기명
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodInfo {
        private FragmentPeriodKey periodKey;  // 기간 필터링
        private LocalDate from;               // 시작일
        private LocalDate to;                 // 종료일
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private long totalCount;              // 조각함+기간 기준 총 조각 수
        private String boxTypeName;           // 조각함 타입 화면 표기용
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fragment {
        private LocalDate recordDate;         // 기록 날짜( YYYY-MM-DD)
        private Long fragmentId;              // 조각
        private StockInfo stock;              // 종목 명
        private BigDecimal unitPrice;         // 단위당 가격
        private BigDecimal quantity;          // 수량
        private String memo;                  // 메모
        private EmotionCode emotionCode;      // 감정 코드
        private String emotionName;           // 감정 한글명
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockInfo {
        private Long stockId;                 // 종목 PK
        private String stockName;             // 종목명
        private TradeAction tradeAction;      // 매수, 매도, 관망
    }
}
