package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.ConvictionScoreConverter;
import com.umc.finly.domain.analysis.association.dto.ConvictionScoreResDTO;
import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.exception.ConvictionScoreErrorCode;
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
        ConvictionScoreResult currentResult = convictionScoreResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .orElseThrow(() -> new ConvictionScoreException(ConvictionScoreErrorCode.CONVICTION_SCORE_NOT_FOUND));

        return ConvictionScoreConverter.toConvictionScoreResDTO(currentResult.getConvictionScore());
    }

}
