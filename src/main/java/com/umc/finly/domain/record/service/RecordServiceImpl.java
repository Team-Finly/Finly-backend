package com.umc.finly.domain.record.service;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.converter.RecordConverter;
import com.umc.finly.domain.record.dto.request.RecordCreateReqDTO;
import com.umc.finly.domain.record.dto.request.RecordUpdateReqDTO;
import com.umc.finly.domain.record.dto.response.DailyReportResDTO;
import com.umc.finly.domain.record.dto.response.RecentSearchResDTO;
import com.umc.finly.domain.record.dto.response.RecordCreateResDTO;
import com.umc.finly.domain.record.dto.response.RecordDetailResDTO;
import com.umc.finly.domain.record.dto.response.RecordSearchResDTO;
import com.umc.finly.domain.record.dto.response.RecordUpdateResDTO;
import com.umc.finly.domain.record.dto.response.TodayRecordResDTO;
import com.umc.finly.domain.record.exception.code.RecordErrorCode;
import com.umc.finly.domain.record.infra.OpenAiFeedbackClient;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.entity.SearchHistory;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.domain.record.repository.SearchHistoryRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.domain.market.exception.code.MarketErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 투자 기록(Record) 비즈니스 로직 구현체
// 기록 CRUD, 검색, AI 피드백 요청, 데일리 리포트 등을 처리함
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 사용
public class RecordServiceImpl implements RecordService {

    private final RecordConverter recordConverter;
    private final RecordEntryRepository recordEntryRepository;
    private final RecordFeedbackService feedbackService; // AI 피드백 서비스
    private final StockRepository stockRepository;
    private final OpenAiFeedbackClient openAiFeedbackClient; // OpenAI API 클라이언트
    private final SearchHistoryRepository searchHistoryRepository;

    private static final int RECENT_SEARCH_LIMIT = 3; // 최근 검색어 최대 개수

    @Override
    @Transactional
    public RecordCreateResDTO createRecord(Long memberId, RecordCreateReqDTO request) {
        // 1. clientRequestId 중복 체크 (멱등성 보장용)
        if (recordEntryRepository.existsByClientRequestId(request.getClientRequestId())) {
            throw new CustomException(RecordErrorCode.RECORD_DUPLICATE_SUBMISSION);
        }

        // 2. 매수/매도 시 가격과 수량 필수 검증
        if (request.getTradeAction() == TradeAction.BUY || request.getTradeAction() == TradeAction.SELL) {
            if (request.getUnitPrice() == null || request.getQuantity() == null) {
                throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
            }
            if (request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0
                    || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
            }
        }

        // 3. symbol로 종목(Stock) 조회
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));

        // 4. 현재 시간 기반으로 세션(장전/오전 등) 자동 계산
        Session session = Session.fromTime(LocalTime.now());

        // 5. RecordEntry 엔티티 생성 및 저장
        RecordEntry entry = recordConverter.toEntity(memberId, request, stock, session);

        RecordEntry savedEntry;
        try {
            savedEntry = recordEntryRepository.save(entry);
        } catch (DataIntegrityViolationException e) {
            // DB 레벨 유니크 제약 위반 시 (동시 요청 대응)
            throw new CustomException(RecordErrorCode.RECORD_DUPLICATE_SUBMISSION);
        }

        // 6. AI 피드백 비동기 생성 요청 (트랜잭션 커밋 후 실행됨)
        RecordFeedback feedback = feedbackService.requestFeedbackAsync(memberId, savedEntry);

        // 7. 응답 DTO 반환
        return recordConverter.toCreateRes(savedEntry, stock, feedback);
    }

    @Override
    public RecordDetailResDTO getRecord(Long memberId, Long recordId) {
        // 1. 기록 조회
        RecordEntry entry = recordEntryRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        // 2. 본인 기록인지 권한 확인
        if (!entry.getMemberId().equals(memberId)) {
            throw new CustomException(RecordErrorCode.RECORD_FORBIDDEN);
        }

        // 3. 종목 정보 조회
        Stock stock = stockRepository.findById(entry.getStockId())
                .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));

        // 4. 응답 DTO 반환
        return recordConverter.toDetailRes(entry, stock);
    }

    @Override
    @Transactional
    public RecordUpdateResDTO updateRecord(Long memberId, Long recordId, RecordUpdateReqDTO request) {
        // 1. 기록 조회
        RecordEntry entry = recordEntryRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        // 2. 본인 기록인지 권한 확인
        if (!entry.getMemberId().equals(memberId)) {
            throw new CustomException(RecordErrorCode.RECORD_FORBIDDEN);
        }

        // 3. 부분 업데이트 (symbol 변경 시 stock 조회)
        Stock changedStock = null;
        if (request.getSymbol() != null) {
            changedStock = stockRepository.findBySymbol(request.getSymbol())
                    .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));
        }

        // 3-1. 수정 내용 반영 (변환 책임은 컨버터로)
        recordConverter.applyUpdate(entry, request, changedStock);

        // 4. 수정 후에도 매수/매도 유효성 검증
        if (entry.getTradeAction() == TradeAction.BUY || entry.getTradeAction() == TradeAction.SELL) {
            if (entry.getUnitPrice() == null || entry.getQuantity() == null) {
                throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
            }
            if (entry.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0
                    || entry.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
            }
        }

        // 5. symbol 변경 없으면 기존 Stock 조회
        Stock stock = changedStock;
        if (stock == null) {
            stock = stockRepository.findById(entry.getStockId())
                    .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));
        }

        // 6. 응답 DTO 반환
        return recordConverter.toUpdateRes(entry, stock);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 외부 API 호출이라 트랜잭션 제외
    public DailyReportResDTO getDailyReport(Long memberId, Long recordId) {
        // 1. 기록 조회
        RecordEntry entry = recordEntryRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        // 2. 본인 기록인지 권한 확인
        if (!entry.getMemberId().equals(memberId)) {
            throw new CustomException(RecordErrorCode.RECORD_FORBIDDEN);
        }

        // 3. 종목 정보 조회
        Stock stock = stockRepository.findById(entry.getStockId())
                .orElseThrow(() -> new CustomException(MarketErrorCode.MARKET_STOCK_NOT_FOUND));

        // 4. 메모가 있으면 OpenAI로 요약, 없으면 빈 문자열
        String content = "";
        if (entry.getMemo() != null && !entry.getMemo().isBlank()) {
            String systemPrompt = "당신은 투자 기록 메모를 간결하게 요약하는 도우미입니다. 주어진 메모를 한 줄로 요약해 주세요.";
            try {
                OpenAiFeedbackClient.FeedbackResponse response =
                        openAiFeedbackClient.generateFeedback(systemPrompt, entry.getMemo());
                content = response.content();
            } catch (Exception e) {
                // API 호출 실패 시 원본 메모 그대로 반환
                content = entry.getMemo();
            }
        }

        return recordConverter.toDailyReportRes(entry, stock, content);
    }

    @Override
    public TodayRecordResDTO getTodayRecords(Long memberId, LocalDate date) {
        // 1. 해당 날짜의 기록 조회 (생성 시간 오름차순)
        List<RecordEntry> entries = recordEntryRepository
                .findByMemberIdAndRecordDateOrderByCreatedAtAsc(memberId, date);

        // 2. N+1 방지: 기록에 포함된 stockId 일괄 조회
        List<Long> stockIds = entries.stream()
                .map(RecordEntry::getStockId)
                .distinct()
                .toList();
        Map<Long, Stock> stockMap = stockRepository.findAllById(stockIds).stream()
                .collect(Collectors.toMap(Stock::getId, Function.identity()));

        // 3. 응답 DTO 변환 (converter에 위임)
        return recordConverter.toTodayRecordRes(date, entries, stockMap);
    }

    @Override
    @Transactional // 검색 기록 저장 때문에 쓰기 트랜잭션 필요
    public RecordSearchResDTO searchRecords(Long memberId, String keyword, EmotionCode emotionCode) {
        // 1. 키워드 정규화 (앞뒤 공백 제거, 빈 문자열은 null 처리)
        String normalizedKeyword = (keyword == null) ? null : keyword.strip();
        if (normalizedKeyword != null && normalizedKeyword.isBlank()) {
            normalizedKeyword = null;
        }

        // 2. 키워드가 있으면 검색 기록 저장 (중복 시 시간만 갱신)
        if (normalizedKeyword != null) {
            saveSearchHistory(memberId, normalizedKeyword);
        }

        // 3. 키워드로 종목명 매칭되는 stockId 목록 조회
        List<Long> stockIds = null;
        if (normalizedKeyword != null) {
            stockIds = stockRepository.findByNameContaining(normalizedKeyword).stream()
                    .map(Stock::getId)
                    .toList();
            if (stockIds.isEmpty()) {
                stockIds = null;
            }
        }

        // 4. 메모 + 종목명 검색 (OR 조건)
        List<RecordEntry> entries = recordEntryRepository.searchRecords(
                memberId, emotionCode, normalizedKeyword, stockIds);

        // 5. N+1 방지: 결과의 stockId 일괄 조회
        List<Long> resultStockIds = entries.stream()
                .map(RecordEntry::getStockId)
                .distinct()
                .toList();
        Map<Long, Stock> stockMap = stockRepository.findAllById(resultStockIds).stream()
                .collect(Collectors.toMap(Stock::getId, Function.identity()));

        // 6. 응답 DTO 변환 (converter에 위임)
        return recordConverter.toSearchRes(entries, stockMap);
    }

    @Override
    public RecentSearchResDTO getRecentSearchKeywords(Long memberId) {
        // 최근 검색 키워드 조회 (최신순, 상위 N개)
        List<String> recentKeywords = searchHistoryRepository
                .findRecentKeywordsByMemberId(memberId, PageRequest.of(0, RECENT_SEARCH_LIMIT));
        return recordConverter.toRecentSearchRes(recentKeywords);
    }

    @Override
    @Transactional
    public void deleteSearchKeyword(Long memberId, String keyword) {
        // 키워드 정규화 (앞뒤 공백 제거)
        String normalizedKeyword = (keyword == null) ? null : keyword.strip();
        if (normalizedKeyword == null || normalizedKeyword.isBlank()) {
            throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
        }

        // 검색 기록 삭제 (본인 기록만 삭제됨)
        int deletedCount = searchHistoryRepository.deleteByMemberIdAndKeyword(memberId, normalizedKeyword);
        if (deletedCount == 0) {
            throw new CustomException(RecordErrorCode.SEARCH_HISTORY_NOT_FOUND);
        }
    }

    // 검색 키워드 저장 (중복 키워드는 updatedAt만 갱신)
    private void saveSearchHistory(Long memberId, String keyword) {
        searchHistoryRepository.findByMemberIdAndKeyword(memberId, keyword)
                .ifPresentOrElse(
                        // 기존 기록 있으면 updatedAt만 갱신 (touch)
                        existing -> searchHistoryRepository.touchUpdatedAt(existing.getId()),
                        // 기존 기록 없으면 새로 저장
                        () -> {
                            try {
                                SearchHistory history = SearchHistory.builder()
                                        .memberId(memberId)
                                        .keyword(keyword)
                                        .build();
                                searchHistoryRepository.save(history);
                            } catch (DataIntegrityViolationException e) {
                                // 동시 요청으로 이미 저장된 경우 updatedAt만 갱신
                                searchHistoryRepository.findByMemberIdAndKeyword(memberId, keyword)
                                        .ifPresent(existing -> searchHistoryRepository.touchUpdatedAt(existing.getId()));
                            }
                        }
                );
    }
}
