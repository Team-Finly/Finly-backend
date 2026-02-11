package com.umc.finly.domain.record.converter;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.dto.request.RecordCreateReqDTO;
import com.umc.finly.domain.record.dto.request.RecordUpdateReqDTO;
import com.umc.finly.domain.record.dto.response.RecordCreateResDTO;
import com.umc.finly.domain.record.dto.response.RecordDetailResDTO;
import com.umc.finly.domain.record.dto.response.RecordUpdateResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        return RecordCreateResDTO.from(entry, stock, feedback);
    }

    // 상세 응답 DTO로 변환
    public RecordDetailResDTO toDetailRes(RecordEntry entry, Stock stock) {
        return RecordDetailResDTO.from(entry, stock);
    }

    // 수정 응답 DTO로 변환
    public RecordUpdateResDTO toUpdateRes(RecordEntry entry, Stock stock) {
        return RecordUpdateResDTO.from(entry, stock);
    }
}
