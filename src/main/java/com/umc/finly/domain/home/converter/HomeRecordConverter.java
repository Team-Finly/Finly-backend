package com.umc.finly.domain.home.converter;

import com.umc.finly.domain.home.dto.response.HomeRecordsResDTO;
import com.umc.finly.domain.record.dto.response.RecordSearchResDTO;

public class HomeRecordConverter {

    private HomeRecordConverter() {
    }

    public static HomeRecordsResDTO.Record toHomeRecord(
            RecordSearchResDTO.SearchEntry r
    ) {
        return HomeRecordsResDTO.Record.builder()
                .recordId(r.getRecordId())
                .recordDate(r.getRecordDate())
                .recordedAt(r.getRecordedAt())
                .session(r.getSession())
                .tradeAction(r.getTradeAction())
                .symbol(r.getSymbol())
                .unitPrice(r.getUnitPrice())
                .quantity(r.getQuantity())
                .emotionCode(r.getEmotionCode())
                .emotionIntensity(r.getEmotionIntensity())
                .memo(r.getMemo())
                .build();
    }
}
