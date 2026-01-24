package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.RecordCreateReq;
import com.umc.finly.domain.record.dto.RecordCreateRes;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.Session;
import com.umc.finly.domain.record.entity.TradeAction;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordServiceImpl implements RecordService {

    private final RecordEntryRepository recordEntryRepository;

    @Override
    @Transactional
    public RecordCreateRes createRecord(Long userId, RecordCreateReq request) {
        // 1. clientRequestId 중복 체크
        if (recordEntryRepository.existsByClientRequestId(request.getClientRequestId())) {
            throw new CustomException(ErrorCode.RECORD_DUPLICATE_SUBMISSION);
        }

        // 2. tradeAction이 BUY/SELL이면 unitPrice, quantity 필수 검증
        if (request.getTradeAction() == TradeAction.BUY || request.getTradeAction() == TradeAction.SELL) {
            if (request.getUnitPrice() == null || request.getQuantity() == null) {
                throw new CustomException(ErrorCode.RECORD_INVALID_REQUEST);
            }
        }

        // 3. Session 자동 계산 (현재 시간 기반)
        Session session = Session.fromTime(LocalTime.now());

        // 4. RecordEntry 엔티티 생성 및 저장
        RecordEntry entry = RecordEntry.builder()
                .userId(userId)
                .clientRequestId(request.getClientRequestId())
                .recordDate(request.getRecordDate())
                .stockId(request.getStockId())
                .tradeAction(request.getTradeAction())
                .unitPrice(request.getUnitPrice())
                .quantity(request.getQuantity())
                .emotionCode(request.getEmotionCode())
                .emotionIntensity(request.getEmotionIntensity())
                .memo(request.getMemo())
                .session(session)
                .build();

        RecordEntry savedEntry = recordEntryRepository.save(entry);

        // 5. 응답 DTO 반환
        return RecordCreateRes.from(savedEntry);
    }
}
