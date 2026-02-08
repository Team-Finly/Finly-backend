package com.umc.finly.domain.record.converter;

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

@Component
@RequiredArgsConstructor
public class FragmentConverter {

    // 조각 모음함 요약 응답 DTO 변환
    public FragmentSummaryResDTO toFragmentSummaryRes(
            long totalCount,
            EmotionCode dominantType,
            List<FragmentSummaryResDTO.TypeSummary> summaries) {
        return FragmentSummaryResDTO.builder()
                .totalCount((int) totalCount)
                .dominantType(dominantType)
                .typeSummary(summaries)
                .build();
    }

    // 조각 리스트 응답 DTO 변환(헤더/요약/아이템까지 조립)
    public FragmentListResDTO toFragmentListRes(
            EmotionCode boxType,
            FragmentPeriodKey periodKey,
            LocalDate from,
            LocalDate to,
            long totalCount,
            List<FragmentRepository.FragmentListRowView> rows
    ) {
        // 감정 라벨 계산(필터 없으면 "전체")
        String boxLabel = (boxType == null) ? "전체" : boxType.getLabel();

        return FragmentListResDTO.builder()
                .box(FragmentListResDTO.BoxInfo.builder()
                        .boxType(boxType)
                        .boxTypeName(boxLabel)
                        .build())
                .period(FragmentListResDTO.PeriodInfo.builder()
                        .periodKey(periodKey)
                        .from(from)
                        .to(to)
                        .build())
                .summary(FragmentListResDTO.Summary.builder()
                        .totalCount(totalCount)
                        .boxTypeName(boxLabel)
                        .build())
                .fragments(rows.stream()
                        .map(this::toFragment)
                        .collect(Collectors.toList()))
                .build();
    }

    // Projection Row -> Fragment DTO로 변환
    private FragmentListResDTO.Fragment toFragment(FragmentRepository.FragmentListRowView r) {
        EmotionCode emotion = r.getEmotionCode();

        return FragmentListResDTO.Fragment.builder()
                .recordDate(r.getRecordDate())
                .fragmentId(r.getFragmentId())
                .stock(FragmentListResDTO.StockInfo.builder()
                        .stockId(r.getStockId())
                        .stockName(r.getStockName())
                        .tradeAction(r.getTradeAction())
                        .build())
                .unitPrice(r.getUnitPrice())
                .quantity(r.getQuantity())
                .memo(r.getMemo())
                .emotionCode(emotion)
                .emotionName(emotion == null ? null : emotion.getLabel())
                .build();
    }
}
