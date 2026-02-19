package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.ConvictionScoreConverter;
import com.umc.finly.domain.analysis.association.dto.response.ConvictionScoreResDTO;
import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.enums.Status;
import com.umc.finly.domain.analysis.association.exception.code.ConvictionScoreErrorCode;
import com.umc.finly.domain.analysis.association.exception.ConvictionScoreException;
import com.umc.finly.domain.analysis.association.repository.ConvictionScoreResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ConvictionScoreServiceImpl implements ConvictionScoreService {

    private final ConvictionScoreResultRepository convictionScoreResultRepository;

    @Override
    @Transactional
    public void calculateAndSave(Long memberId, LocalDate start, LocalDate end) {
        // TODO: 1. 분석 기간 내 사용자의 매수 기록 조회
        // TODO: 2. T+5 주가 비교를 통한 적중 여부 판별 및 ConvictionScoreDetail 저장
        // TODO: 3. 통계 계산(적중률 등) 및 ConvictionScoreResult 저장
    }

    @Override
    public ConvictionScoreResDTO getConvictionScore(Long memberId) {
        // 가장 최신 매수 확신도 결과 조회
        return convictionScoreResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .map(currentResult ->
                        ConvictionScoreConverter.toConvictionScoreResDTO(currentResult.getConvictionScore())
                )
                // 결과 데이터가 없을 경우 기본값 반환
                .orElseGet(() -> ConvictionScoreResDTO.builder()
                        .convictionScore(0)
                        .status(Status.LOW)
                        .phrase("분석을 위해 데이터를 모으는 중이에요")
                        .build());
    }

}
