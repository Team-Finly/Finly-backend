package com.umc.finly.domain.analysis.emotion.service;

import com.umc.finly.domain.analysis.emotion.converter.EmotionAnalysisConverter;
import com.umc.finly.domain.analysis.emotion.dto.response.EmotionDistributionResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.GoldenTimeResDTO;
import com.umc.finly.domain.analysis.emotion.dto.response.ShakenKeywordsResDTO;
import com.umc.finly.domain.analysis.emotion.exception.code.EmotionAnalysisErrorCode;
import com.umc.finly.domain.analysis.emotion.repository.EmotionAnalysisRepository;
import com.umc.finly.domain.analysis.emotion.util.KeywordExtractor;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    private final StockRepository stockRepository;
    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final EmotionAnalysisConverter emotionAnalysisConverter;

    // 키워드 추출 상위 8개
    private static final int MAX_KEYWORDS = 8;

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

    @Override
    public ShakenKeywordsResDTO getShakenKeywords(Long memberId, String symbol) {
        // memberId가 없으면 잘못된 요청으로 처리
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // symbol 검증
        if (symbol == null || symbol.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 종목 조회
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new CustomException(EmotionAnalysisErrorCode.ANALYSIS_STOCK_NOT_FOUND));

        // 기록 조회(매수/매도 모두 포함)
        List<RecordEntry> aliveRecords  = emotionAnalysisRepository.findAllByMemberIdAndStockIdAndDeletedAtIsNull(memberId, stock.getId());

        // DF(Document Frequency): 메모 1개에서 토큰은 1번만 카운트
        Map<String, Integer> dfCountMap = new HashMap<>();

        for (RecordEntry r : aliveRecords) {
            // 메모에서 중복 제거된 토큰 추출
            Set<String> tokens = KeywordExtractor.extractUniqueTokens(r.getMemo());

            for (String token : tokens) {
                dfCountMap.merge(token, 1, Integer::sum);
            }
        }

        // 정렬(DF 내림차순, 키워드 오름차순)
        List<Map.Entry<String, Integer>> rankedKeywords = new ArrayList<>(dfCountMap.entrySet());
        rankedKeywords.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            return (cmp != 0) ? cmp : a.getKey().compareTo(b.getKey());
        });

        // 상위 8개만 잘라서 내려주기
        if (rankedKeywords.size() > MAX_KEYWORDS) {
            rankedKeywords = rankedKeywords.subList(0, MAX_KEYWORDS);
        }

        // converter로 응답 조립
        return emotionAnalysisConverter.toShakenKeywordsResDTO(
                stock,
                rankedKeywords
        );
    }

    @Override
    public GoldenTimeResDTO getEmotionGoldenTime(Long memberId, String symbol) {

        // memberId가 없으면 인증 실패
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // symbol 검증
        if (symbol == null || symbol.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 종목 조회
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new CustomException(EmotionAnalysisErrorCode.ANALYSIS_STOCK_NOT_FOUND));

        // 세션별 count 집계
        List<EmotionAnalysisRepository.SessionCountProjection> projections =
                emotionAnalysisRepository.countGroupBySession(memberId, stock.getId());

        // 세션별 count 맵 (없는 세션은 0으로)
        Map<Session, Integer> countMap = new EnumMap<>(Session.class);
        for (Session s : Session.values()) {
            //기본값 0 세팅
            countMap.put(s, 0);
        }

        for (EmotionAnalysisRepository.SessionCountProjection p : projections) {
            // group by 결과 반영
            countMap.put(p.getSession(), (int) p.getCount());
        }

        // totalRecords 계산
        int totalRecords = 0;
        for (Session s : Session.values()) {
            totalRecords += countMap.get(s);
        }

        // percent 계산(버림)
        Map<Session, Integer> percentMap = new EnumMap<>(Session.class);
        int percentSum = 0;

        // 골든타임 선정 (기록 0이면 null)
        Session goldenTime = (totalRecords == 0) ? null : pickGoldenTime(countMap);

        if (totalRecords == 0) {
            // 기록이 없으면 모두 0%
            for (Session s : Session.values()) {
                percentMap.put(s, 0);
            }
        } else {
            for (Session s : Session.values()) {
                int percent = (countMap.get(s) * 100) / totalRecords;
                percentMap.put(s, percent);
                percentSum += percent;
            }

            // 합이 100이 되도록 보정(diff를 골든타임에 몰아주기)
            int diff = 100 - percentSum;
            percentMap.put(goldenTime, percentMap.get(goldenTime) + diff);
        }

        // sessions 리스트 생성(항상 4개 내려줌)
        List<GoldenTimeResDTO.Sessions> sessions = new ArrayList<>();
        for (Session s : Session.values()) {
            sessions.add(emotionAnalysisConverter.toSessionSummary(
                    s,
                    countMap.get(s),
                    percentMap.get(s)
            ));
        }

        // Converter로 최종 응답 조립
        return emotionAnalysisConverter.toGoldenTimeResDTO(
                stock,
                totalRecords,
                goldenTime,
                sessions
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

    private Session pickGoldenTime(Map<Session, Integer> countMap) {
        // 동률이면 MORNING > AFTERNOON > PRE_MARKET > POST_MARKET 우선
        Session[] priority = {
                Session.MORNING,
                Session.AFTERNOON,
                Session.PRE_MARKET,
                Session.POST_MARKET
        };

        Session best = priority[0];
        int bestCount = -1;

        for (Session s : priority) {
            int count = countMap.getOrDefault(s, 0);
            if (count > bestCount) {
                bestCount = count;
                best = s;
            }
        }

        return best;
    }
}