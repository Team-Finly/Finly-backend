package com.umc.finly.domain.analysis.emotion.service;

import com.umc.finly.domain.analysis.emotion.converter.EmotionAnalysisConverter;
import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.exception.code.EmotionAnalysisErrorCode;
import com.umc.finly.domain.analysis.emotion.repository.EmotionAnalysisRepository;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    private final StockRepository stockRepository;
    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final EmotionAnalysisConverter emotionAnalysisConverter;

    @Override
    public EmotionDistributionResDTO getEmotionDistribution(Long memberId, String symbol) {

        // memberId가 없으면 잘못된 요청으로 처리
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // symbol 검증
        if (symbol == null || symbol.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // symbol로 Stock을 찾아서 stockId/stockName 확보
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new CustomException(EmotionAnalysisErrorCode.ANALYSIS_STOCK_NOT_FOUND));

        // 종목 DTO 생성
        EmotionDistributionResDTO.SelectedStock stockDto =
                EmotionDistributionResDTO.SelectedStock.builder()
                        .symbol(stock.getSymbol())
                        .stockName(stock.getName())
                        .build();

        // 감정별 count 집계 조회
        List<EmotionAnalysisRepository.EmotionCountProjection> projections =
                emotionAnalysisRepository.countGroupByEmotionCode(memberId, stock.getId());

        // emotionCode -> count 맵으로 변환
        Map<EmotionCode, Long> countMap = new EnumMap<>(EmotionCode.class);
        long totalCount = 0;

        for (EmotionAnalysisRepository.EmotionCountProjection p : projections) {
            // null 방어해서 count 추출
            long count = (p.getCount() == null) ? 0L : p.getCount();

            // 감정별 count 저장
            countMap.put(p.getEmotionCode(), count);

            // 전체 개수 누적
            totalCount += count;
        }

        // 모든 EmotionCode를 포함하는 summary 리스트 생성(0건도 포함)
        List<EmotionDistributionResDTO.TypeSummary> summaries = new ArrayList<>();

        for (EmotionCode code : EmotionCode.values()) {
            // 해당 감정 count 가져오기(없으면 0)
            long count = countMap.getOrDefault(code, 0L);

            // percent 계산(버림)
            int percent = 0;
            if (totalCount > 0) {
                percent = (int) ((count * 100) / totalCount);
            }

            // TypeSummary 생성
            summaries.add(EmotionDistributionResDTO.TypeSummary.builder()
                    .type(code)
                    .count(count)
                    .percent(percent)
                    .build());
        }

        // percent 합이 100이 되도록 보정 (분배 방식)
        adjustPercentTo100(totalCount, summaries);

        // Converter로 최종 응답 조립
        return emotionAnalysisConverter.toEmotionDistributionResDTO(
                totalCount,
                stockDto,
                summaries
        );
    }

    private void adjustPercentTo100(long totalCount, List<EmotionDistributionResDTO.TypeSummary> summaries) {
        if (totalCount == 0 || summaries == null || summaries.isEmpty()) return;

        int sum = 0;
        for (var s : summaries) sum += s.getPercent();

        int diff = 100 - sum;
        if (diff == 0) return;

        // 타깃 선정용 복사 리스트 (원본 순서 보존)
        List<EmotionDistributionResDTO.TypeSummary> targets = new ArrayList<>(summaries);

        if (diff > 0) {
            // 부족: count 큰 순(+), 단 0건에 퍼센트 주는 게 싫으면 필터링 가능
            targets.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

            for (int i = 0; i < diff; i++) {
                var t = targets.get(i % targets.size());
                t.setPercent(t.getPercent() + 1);
            }
        } else {
            // 초과: percent가 0보다 큰 애들에서만 -1 해야 음수 방지
            int remaining = -diff;

            // (1) count 작은 순으로 먼저 깎기
            targets.sort((a, b) -> Long.compare(a.getCount(), b.getCount()));

            int idx = 0;
            int guard = 0; // 무한루프 방지
            while (remaining > 0 && guard < 10_000) {
                var t = targets.get(idx % targets.size());
                if (t.getPercent() > 0) {
                    t.setPercent(t.getPercent() - 1);
                    remaining--;
                }
                idx++;
                guard++;
            }
        }
    }
}