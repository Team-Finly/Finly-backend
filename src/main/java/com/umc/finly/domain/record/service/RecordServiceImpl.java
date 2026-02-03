package com.umc.finly.domain.record.service;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.dto.DailyReportRes;
import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.dto.RecordDetailRes;
import com.umc.finly.domain.record.dto.RecordUpdateReq;
import com.umc.finly.domain.record.dto.RecordUpdateRes;
import com.umc.finly.domain.record.infra.OpenAiFeedbackClient;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.Session;
import com.umc.finly.domain.record.enums.TradeAction;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordServiceImpl implements RecordService {

    private final RecordEntryRepository recordEntryRepository;
    private final RecordFeedbackService feedbackService;
    private final StockRepository stockRepository;
    private final OpenAiFeedbackClient openAiFeedbackClient;

    @Override
    @Transactional
    public RecordCreateRes createRecord(Long memberId, RecordCreateReq request) {
        // 1. clientRequestId 중복 체크
        if (recordEntryRepository.existsByClientRequestId(request.getClientRequestId())) {
            throw new CustomException(ErrorCode.RECORD_DUPLICATE_SUBMISSION);
        }

        // 2. tradeAction이 BUY/SELL이면 unitPrice, quantity 필수 및 0보다 커야 함
        if (request.getTradeAction() == TradeAction.BUY || request.getTradeAction() == TradeAction.SELL) {
            if (request.getUnitPrice() == null || request.getQuantity() == null) {
                throw new CustomException(ErrorCode.RECORD_INVALID_REQUEST);
            }
            if (request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0
                    || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(ErrorCode.RECORD_INVALID_REQUEST);
            }
        }

        // 3. symbol로 Stock 조회
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new CustomException(ErrorCode.MARKET_STOCK_NOT_FOUND));

        // 4. Session 자동 계산 (현재 시간 기반)
        Session session = Session.fromTime(LocalTime.now());

        // 5. RecordEntry 엔티티 생성 및 저장
        RecordEntry entry = RecordEntry.builder()
                .memberId(memberId)
                .clientRequestId(request.getClientRequestId())
                .recordDate(request.getRecordDate())
                .stockId(stock.getId())
                .tradeAction(request.getTradeAction())
                .unitPrice(request.getUnitPrice())
                .quantity(request.getQuantity())
                .emotionCode(request.getEmotionCode())
                .emotionIntensity(request.getEmotionIntensity())
                .memo(request.getMemo())
                .session(session)
                .build();

        RecordEntry savedEntry;
        try {
            savedEntry = recordEntryRepository.save(entry);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.RECORD_DUPLICATE_SUBMISSION);
        }

        // 6. AI 피드백 비동기 생성 요청
        RecordFeedback feedback = feedbackService.requestFeedbackAsync(memberId, savedEntry);

        // 7. 응답 DTO 반환
        return RecordCreateRes.from(savedEntry, stock, feedback);
    }

    @Override
    public RecordDetailRes getRecord(Long memberId, Long recordId) {
        // 1. 기록 조회
        RecordEntry entry = recordEntryRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        // 2. 본인 기록인지 확인
        if (!entry.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.RECORD_FORBIDDEN);
        }

        // 3. Stock 조회
        Stock stock = stockRepository.findById(entry.getStockId())
                .orElseThrow(() -> new CustomException(ErrorCode.MARKET_STOCK_NOT_FOUND));

        // 4. 응답 DTO 반환
        return RecordDetailRes.from(entry, stock);
    }

    @Override
    @Transactional
    public RecordUpdateRes updateRecord(Long memberId, Long recordId, RecordUpdateReq request) {
        // 1. 기록 조회
        RecordEntry entry = recordEntryRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        // 2. 본인 기록인지 확인
        if (!entry.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.RECORD_FORBIDDEN);
        }

        // 3. 부분 업데이트 (null이 아닌 필드만 수정)
        Stock stock = null;
        if (request.getRecordDate() != null) {
            entry.setRecordDate(request.getRecordDate());
        }
        if (request.getSymbol() != null) {
            stock = stockRepository.findBySymbol(request.getSymbol())
                    .orElseThrow(() -> new CustomException(ErrorCode.MARKET_STOCK_NOT_FOUND));
            entry.setStockId(stock.getId());
        }
        if (request.getTradeAction() != null) {
            entry.setTradeAction(request.getTradeAction());
        }
        if (request.getUnitPrice() != null) {
            entry.setUnitPrice(request.getUnitPrice());
        }
        if (request.getQuantity() != null) {
            entry.setQuantity(request.getQuantity());
        }
        if (request.getEmotionCode() != null) {
            entry.setEmotionCode(request.getEmotionCode());
        }
        if (request.getEmotionIntensity() != null) {
            entry.setEmotionIntensity(request.getEmotionIntensity());
        }
        if (request.getMemo() != null) {
            entry.setMemo(request.getMemo());
        }

        // 4. tradeAction이 BUY/SELL이면 unitPrice, quantity 필수 및 0보다 커야 함
        if (entry.getTradeAction() == TradeAction.BUY || entry.getTradeAction() == TradeAction.SELL) {
            if (entry.getUnitPrice() == null || entry.getQuantity() == null) {
                throw new CustomException(ErrorCode.RECORD_INVALID_REQUEST);
            }
            if (entry.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0
                    || entry.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(ErrorCode.RECORD_INVALID_REQUEST);
            }
        }

        // 5. symbol이 변경되지 않은 경우 기존 Stock 조회
        if (stock == null) {
            stock = stockRepository.findById(entry.getStockId())
                    .orElseThrow(() -> new CustomException(ErrorCode.MARKET_STOCK_NOT_FOUND));
        }

        // 6. 응답 DTO 반환
        return RecordUpdateRes.from(entry, stock);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public DailyReportRes getDailyReport(Long memberId, Long recordId) {
        // 1. 기록 조회
        RecordEntry entry = recordEntryRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        // 2. 본인 기록인지 확인
        if (!entry.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.RECORD_FORBIDDEN);
        }

        // 3. Stock 조회
        Stock stock = stockRepository.findById(entry.getStockId())
                .orElseThrow(() -> new CustomException(ErrorCode.MARKET_STOCK_NOT_FOUND));

        // 4. memo 요약 (빈 값이면 OpenAI 호출 스킵)
        String content = "";
        if (entry.getMemo() != null && !entry.getMemo().isBlank()) {
            String systemPrompt = "당신은 투자 기록 메모를 간결하게 요약하는 도우미입니다. 주어진 메모를 한 줄로 요약해 주세요.";
            try {
                OpenAiFeedbackClient.FeedbackResponse response =
                        openAiFeedbackClient.generateFeedback(systemPrompt, entry.getMemo());
                content = response.content();
            } catch (Exception e) {
                content = entry.getMemo();
            }
        }

        return DailyReportRes.from(entry, stock, content);
    }
}
