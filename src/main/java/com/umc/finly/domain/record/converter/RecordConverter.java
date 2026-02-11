package com.umc.finly.domain.record.converter;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.dto.request.RecordCreateReqDTO;
import com.umc.finly.domain.record.dto.request.RecordUpdateReqDTO;
import com.umc.finly.domain.record.dto.response.DailyReportResDTO;
import com.umc.finly.domain.record.dto.response.RecentSearchResDTO;
import com.umc.finly.domain.record.dto.response.RecordCreateResDTO;
import com.umc.finly.domain.record.dto.response.RecordDetailResDTO;
import com.umc.finly.domain.record.dto.response.RecordSearchResDTO;
import com.umc.finly.domain.record.dto.response.RecordUpdateResDTO;
import com.umc.finly.domain.record.dto.response.TodayRecordResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecordConverter {

    // 기록 생성 요청 DTO를 RecordEntry 엔티티로 변환
    public RecordEntry toEntity(Long memberId, RecordCreateReqDTO req, Stock stock, Session session) {
        return RecordEntry.builder()
                .memberId(memberId)
                .clientRequestId(req.getClientRequestId())
                .recordDate(req.getRecordDate())
                .stockId(stock.getId())
                .tradeAction(req.getTradeAction())
                .unitPrice(req.getUnitPrice())
                .quantity(req.getQuantity())
                .emotionCode(req.getEmotionCode())
                .emotionIntensity(req.getEmotionIntensity())
                .memo(req.getMemo())
                .session(session)
                .build();
    }

    // 기록 수정 요청 DTO를 기존 RecordEntry에 부분 반영
    public void applyUpdate(RecordEntry entry, RecordUpdateReqDTO req, Stock stockOrNull) {
        if (req.getRecordDate() != null) entry.setRecordDate(req.getRecordDate());
        if (req.getTradeAction() != null) entry.setTradeAction(req.getTradeAction());
        if (req.getUnitPrice() != null) entry.setUnitPrice(req.getUnitPrice());
        if (req.getQuantity() != null) entry.setQuantity(req.getQuantity());
        if (req.getEmotionCode() != null) entry.setEmotionCode(req.getEmotionCode());
        if (req.getEmotionIntensity() != null) entry.setEmotionIntensity(req.getEmotionIntensity());
        if (req.getMemo() != null) entry.setMemo(req.getMemo());

        // symbol 변경이 있는 경우에만 stockId 갱신
        if (stockOrNull != null) {
            entry.setStockId(stockOrNull.getId());
        }
    }

    // 생성 응답 DTO로 변환
    public RecordCreateResDTO toCreateRes(RecordEntry entry, Stock stock, RecordFeedback feedback) {
        RecordCreateResDTO.FeedbackInfo feedbackInfo = null;
        if (feedback != null) {
            feedbackInfo = RecordCreateResDTO.FeedbackInfo.builder()
                    .feedbackId(feedback.getId())
                    .status(feedback.getStatus().name())
                    .build();
        }

        return RecordCreateResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(stock.getSymbol())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .feedback(feedbackInfo)
                .build();
    }

    // 상세 응답 DTO로 변환
    public RecordDetailResDTO toDetailRes(RecordEntry entry, Stock stock) {
        return RecordDetailResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(stock.getSymbol())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }

    // 수정 응답 DTO로 변환
    public RecordUpdateResDTO toUpdateRes(RecordEntry entry, Stock stock) {
        return RecordUpdateResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .updatedAt(entry.getUpdatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(stock.getSymbol())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }

    // 일일 리포트 응답 DTO로 변환
    public DailyReportResDTO toDailyReportRes(RecordEntry entry, Stock stock, String content) {
        return DailyReportResDTO.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .name(stock.getName())
                .content(content)
                .build();
    }

    // 오늘 기록 응답 DTO로 변환
    public TodayRecordResDTO toTodayRecordRes(
            LocalDate date,
            List<RecordEntry> entries,
            Map<Long, Stock> stockMap
    ) {
        // 프리즘 피드백 타이틀 생성
        String title = generatePrismTitle(entries);
        TodayRecordResDTO.PrismFeedback prismFeedback = TodayRecordResDTO.PrismFeedback.builder()
                .title(title)
                .generatedAt(LocalDateTime.now())
                .build();

        // 타임라인 엔트리 변환
        List<TodayRecordResDTO.TimelineEntry> timelineSummary = entries.stream()
                .map(entry -> toTimelineEntry(entry, stockMap.get(entry.getStockId())))
                .toList();

        return TodayRecordResDTO.builder()
                .date(date)
                .prismFeedback(prismFeedback)
                .timelineSummary(timelineSummary)
                .hasRecords(!entries.isEmpty())
                .recordCount(entries.size())
                .build();
    }

    // 타임라인 엔트리 변환
    public TodayRecordResDTO.TimelineEntry toTimelineEntry(RecordEntry entry, Stock stock) {
        String symbol = stock != null ? stock.getSymbol() : "";
        return TodayRecordResDTO.TimelineEntry.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(symbol)
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }

    // 프리즘 타이틀 생성 (오늘 기록들의 감정 흐름 요약)
    public String generatePrismTitle(List<RecordEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return "괜찮아요. 기록이 없는 날도 있을 수 있죠.";
        }
        if (entries.size() == 1) {
            EmotionCode emotion = entries.get(0).getEmotionCode();
            if (emotion == null) {
                return "오늘 하루도 기록을 남겼네요!";
            }
            return "{{" + emotion.getLabel() + "}}한 하루네요!";
        }
        EmotionCode first = entries.get(0).getEmotionCode();
        EmotionCode last = entries.get(entries.size() - 1).getEmotionCode();
        if (first == null || last == null) {
            return "오늘도 열심히 기록했네요!";
        }
        return "{{" + first.getLabel() + "}}하게 시작해 {{" + last.getLabel() + "}}" + last.getParticle() + " 마무리한 날이네요.";
    }

    // 검색 응답 DTO로 변환
    public RecordSearchResDTO toSearchRes(List<RecordEntry> entries, Map<Long, Stock> stockMap) {
        List<RecordSearchResDTO.SearchEntry> searchEntries = entries.stream()
                .map(entry -> toSearchEntry(entry, stockMap.get(entry.getStockId())))
                .toList();

        return RecordSearchResDTO.builder()
                .records(searchEntries)
                .totalCount(searchEntries.size())
                .build();
    }

    // 검색 엔트리 변환
    public RecordSearchResDTO.SearchEntry toSearchEntry(RecordEntry entry, Stock stock) {
        String symbol = stock != null ? stock.getSymbol() : "";
        return RecordSearchResDTO.SearchEntry.builder()
                .recordId(entry.getId())
                .recordDate(entry.getRecordDate())
                .recordedAt(entry.getCreatedAt())
                .session(entry.getSession())
                .tradeAction(entry.getTradeAction())
                .symbol(symbol)
                .unitPrice(entry.getUnitPrice())
                .quantity(entry.getQuantity())
                .emotionCode(entry.getEmotionCode())
                .emotionIntensity(entry.getEmotionIntensity())
                .memo(entry.getMemo())
                .build();
    }

    // 최근 검색어 응답 DTO로 변환
    public RecentSearchResDTO toRecentSearchRes(List<String> keywords) {
        return RecentSearchResDTO.builder()
                .recentKeywords(keywords)
                .build();
    }
}
