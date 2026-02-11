package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.response.ConvictionScoreResDTO;

import java.time.LocalDate;

public interface ConvictionScoreService {

    ConvictionScoreResDTO getConvictionScore(Long memberId);

    void calculateAndSave(Long memberId, LocalDate startDate, LocalDate endDate);
}
