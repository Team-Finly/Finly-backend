package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.FearIndexConverter;
import com.umc.finly.domain.analysis.association.dto.FearIndexResDTO;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.analysis.association.exception.FearIndexErrorCode;
import com.umc.finly.domain.analysis.association.exception.FearIndexException;
import com.umc.finly.domain.analysis.association.repository.FearIndexResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FearIndexServiceImpl implements FearIndexService {

    private final FearIndexResultRepository fearIndexResultRepository;

    @Override
    @Transactional
    public void calculateAndSave(Long memberId, LocalDate startDate, LocalDate endDate) {
        // TODO: 1. 분석 기간 내 DownSession 조회
        // TODO: 2. 각 세션별 FearIndexDetail 계산 및 저장
        // TODO: 3. 최종 점수 산출 및 FearIndexResult 저장
    }

    @Override
    public FearIndexResDTO getFearIndex(Long memberId) {
        // 1. 가장 최근에 저장된 분석 결과 조회
        FearIndexResult currentResult = fearIndexResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .orElseThrow(() -> new FearIndexException(FearIndexErrorCode.FEAR_INDEX_NOT_FOUND));

        // 2. 해당 결과의 시작일(startDate)을 기준으로 그보다 이전의 마지막 데이터 조회 (변화량 비교용)
        int prevScore = fearIndexResultRepository.findFirstByMemberIdAndEndDateBeforeOrderByEndDateDesc(
                        memberId, currentResult.getStartDate())
                .map(FearIndexResult::getFearIndex)
                .orElse(currentResult.getFearIndex()); // 이전 기록 없으면 현재와 동일 처리

        // 3. 컨버터를 통해 DTO로 변환하여 반환
        return FearIndexConverter.toFearIndexResDTO(
                currentResult.getFearIndex(),
                prevScore
        );
    }
}
