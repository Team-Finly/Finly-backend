package com.umc.finly.domain.record.converter;

import com.umc.finly.domain.record.dto.res.FragmentCalendarResDTO;
import com.umc.finly.domain.record.dto.res.FragmentListResDTO;
import com.umc.finly.domain.record.dto.res.FragmentSummaryResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;
import com.umc.finly.domain.record.repository.FragmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Fragment 도메인의 Entity/Projection -> DTO 변환을 담당하는 컨버터
// Service 계층에서 호출하여 응답 DTO를 조립함
@Component
@RequiredArgsConstructor
public class FragmentConverter {

    // 조각 모음함 요약 응답 DTO 변환
    // totalCount: 전체 기록 수, dominantType: 가장 많은 감정, summaries: 감정별 통계
    public FragmentSummaryResDTO toFragmentSummaryRes(
            long totalCount,
            EmotionCode dominantType,
            boolean isMultipleDominant,
            EmotionCode recessiveType,
            boolean isMultipleRecessive,
            List<FragmentSummaryResDTO.TypeSummary> summaries) {
        return FragmentSummaryResDTO.builder()
                .totalCount((int) totalCount)
                .dominantType(dominantType)
                .isMultipleDominant(isMultipleDominant)
                .recessiveType(recessiveType)
                .isMultipleRecessive(isMultipleRecessive)
                .typeSummary(summaries)
                .build();
    }

    // 조각 리스트 응답 DTO 변환 (헤더/요약/아이템까지 전체 조립)
    // boxType, periodKey: 필터 조건 / from, to: 기간 범위 / rows: DB 조회 결과
    public FragmentListResDTO toFragmentListRes(
            EmotionCode boxType,
            FragmentPeriodKey periodKey,
            LocalDate from,
            LocalDate to,
            long totalCount,
            List<FragmentRepository.FragmentListRowView> rows
    ) {
        // 감정 라벨 계산 (필터 없으면 "전체"로 표시)
        String boxLabel = (boxType == null) ? "전체" : boxType.getLabel();

        return FragmentListResDTO.builder()
                // 조각함 정보 (선택된 감정 타입)
                .box(FragmentListResDTO.BoxInfo.builder()
                        .boxType(boxType)
                        .boxTypeName(boxLabel)
                        .build())
                // 기간 정보
                .period(FragmentListResDTO.PeriodInfo.builder()
                        .periodKey(periodKey)
                        .from(from)
                        .to(to)
                        .build())
                // 상단 요약 (총 개수, 타입명)
                .summary(FragmentListResDTO.Summary.builder()
                        .totalCount(totalCount)
                        .boxTypeName(boxLabel)
                        .build())
                // 조각 리스트 (Projection -> DTO 변환)
                .fragments(rows.stream()
                        .map(this::toFragment)
                        .collect(Collectors.toList()))
                .build();
    }

    // 캘린더용 응답 DTO 변환
    // yearMonth: yyyy-MM / from~to: 해당 월 범위 / days: 날짜별 집계 결과
    public FragmentCalendarResDTO toCalendarFragment(
            String yearMonth,
            LocalDate from,
            LocalDate to,
            long totalRecords,
            List<FragmentCalendarResDTO.Day> days
    ){
        return FragmentCalendarResDTO.builder()
                .yearMonth(yearMonth)
                .range(FragmentCalendarResDTO.Range.builder()
                        .from(from)
                        .to(to)
                        .build())
                .totalRecords(totalRecords)
                .days(days)
                .build();
    }

    // Repository Projection Row -> Fragment DTO 변환
    // 단일 조각 아이템을 DTO로 매핑함
    private FragmentListResDTO.Fragment toFragment(FragmentRepository.FragmentListRowView r) {
        EmotionCode emotion = r.getEmotionCode();

        return FragmentListResDTO.Fragment.builder()
                .recordDate(r.getRecordDate())
                .fragmentId(r.getFragmentId())
                // 종목 정보 (ID, 이름, 매매 타입)
                .stock(FragmentListResDTO.StockInfo.builder()
                        .stockId(r.getStockId())
                        .stockName(r.getStockName())
                        .tradeAction(r.getTradeAction())
                        .build())
                .unitPrice(r.getUnitPrice())
                .quantity(r.getQuantity())
                .memo(r.getMemo())
                .emotionCode(emotion)
                .emotionName(emotion == null ? null : emotion.getLabel()) // null 방어 처리
                .build();
    }
}
