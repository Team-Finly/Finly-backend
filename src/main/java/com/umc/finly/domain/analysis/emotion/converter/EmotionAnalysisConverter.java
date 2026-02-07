package com.umc.finly.domain.analysis.emotion.converter;

import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.GoldenTimeResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.ShakenKeywordsResDTO;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.enums.Session;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EmotionAnalysisConverter {

    // 감정 분포 그래프 응답 DTO 변환
    public EmotionDistributionResDTO toEmotionDistributionResDTO(
            long totalCount,
            EmotionDistributionResDTO.SelectedStock stock,
            List<EmotionDistributionResDTO.TypeSummary> summaries) {
        return EmotionDistributionResDTO.builder()
                .totalCount((int) totalCount)
                .stock(stock)
                .typeSummary(summaries)
                .build();
    }

    // 나를 흔든 키워드 응답 DTO로 변환
    public static ShakenKeywordsResDTO toShakenKeywordsResDTO(
            Stock stock,
            List<Map.Entry<String, Integer>> rankedKeywords
    ) {
        List<ShakenKeywordsResDTO.KeywordInfo> items = new ArrayList<>();

        for (int i = 0; i < rankedKeywords.size(); i++) {
            Map.Entry<String, Integer> e = rankedKeywords.get(i);

            items.add(ShakenKeywordsResDTO.KeywordInfo.builder()
                    .rank(i + 1)
                    .keyword(e.getKey())
                    .count(e.getValue())
                    .build());
        }

        return ShakenKeywordsResDTO.builder()
                .stock(ShakenKeywordsResDTO.SelectedStock.builder()
                        .symbol(stock.getSymbol())
                        .stockName(stock.getName())
                        .build())
                .keywords(items)
                .build();
    }

    // 감정 골든 타임 응답 DTO 변환
    public GoldenTimeResDTO toGoldenTimeResDTO(
            Stock stock,
            int totalRecords,
            Session goldenTime,
            List<GoldenTimeResDTO.Sessions> sessions
    ) {
        // stock 영역 생성
        GoldenTimeResDTO.SelectedStock stockDto = GoldenTimeResDTO.SelectedStock.builder()
                .symbol(stock.getSymbol())
                .name(stock.getName())
                .build();

        // summary 영역 생성
        GoldenTimeResDTO.Summary summaryDto = GoldenTimeResDTO.Summary.builder()
                .totalRecords(totalRecords)
                .goldenTime(goldenTime)
                .goldenTimeName(goldenTime != null ? goldenTime.getGoldenTimeName() : null)
                .build();

        return GoldenTimeResDTO.builder()
                .stock(stockDto)
                .summary(summaryDto)
                .session(sessions)
                .build();
    }

    // 세션 1개 요약 DTO 생성
    public GoldenTimeResDTO.Sessions toSessionSummary(Session session, int recordCount, int percent) {
        return GoldenTimeResDTO.Sessions.builder()
                .session(session)
                .sessionName(session.getSessionName())
                .recordCount(recordCount)
                .percent(percent)
                .build();
    }
}