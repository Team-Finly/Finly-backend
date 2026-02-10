package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.response.FearIndexResDTO;

import java.time.LocalDate;

public interface FearIndexService {

    FearIndexResDTO getFearIndex(Long memberId);

    void calculateAndSave(Long memberId, LocalDate startDate, LocalDate endDate);
}
