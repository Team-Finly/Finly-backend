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

// 조각 리스트 조회 응답 DTO
// 조각 모음함의 필터링된 기록 리스트
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FragmentListResDTO {

    private BoxInfo box;                      // 현재 선택된 조각함 정보 (감정 타입)
    private PeriodInfo period;                // 적용된 기간 필터 정보
    private Summary summary;                  // 상단 요약 (총 개수, 조각함명)
    private List<Fragment> fragments;         // 조각 리스트 (최신순)

    // 조각함 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxInfo {
        private EmotionCode boxType;          // 조각함 타입 (null이면 전체)
        private String boxTypeName;           // 화면 표기명 (예: "불안", "전체")
    }

    // 기간 필터 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodInfo {
        private FragmentPeriodKey periodKey;  // 기간 키 (ALL, MONTH_1, MONTH_3, MONTH_6, YEAR_1)
        private LocalDate from;               // 시작일 (계산된 날짜)
        private LocalDate to;                 // 종료일 (오늘)
    }

    // 요약 정보 (리스트 상단)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private long totalCount;              // 필터 적용 후 총 조각 수
        private String boxTypeName;           // 조각함 타입명 (화면 표시용)
    }

    // 개별 조각 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fragment {
        private LocalDate recordDate;         // 기록 날짜 (YYYY-MM-DD)
        private Long fragmentId;              // 조각(기록) ID
        private StockInfo stock;              // 종목 정보
        private BigDecimal unitPrice;         // 단가
        private BigDecimal quantity;          // 수량
        private String memo;                  // 메모
        private EmotionCode emotionCode;      // 감정 코드
        private String emotionName;           // 감정 한글명 (예: "불안", "평온")
    }

    // 종목 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockInfo {
        private Long stockId;                 // 종목 PK
        private String stockName;             // 종목명
        private TradeAction tradeAction;      // 매매 타입 (BUY, SELL, WATCH)
    }
}
