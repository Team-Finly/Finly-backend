package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.FearIndexConverter;
import com.umc.finly.domain.analysis.association.dto.response.FearIndexResDTO;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.analysis.association.enums.ChangeDirection;
import com.umc.finly.domain.analysis.association.exception.code.FearIndexErrorCode;
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
        // 가장 최근 분석 결과 조회
        return fearIndexResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .map(currentResult -> {
                    // [결과 데이터가 있는 경우]
                    // 이전 데이터 조회 로직 진행
                    int prevScore = fearIndexResultRepository.findFirstByMemberIdAndEndDateBeforeOrderByEndDateDesc(
                                    memberId, currentResult.getStartDate())
                            .map(FearIndexResult::getFearIndex)
                            .orElse(currentResult.getFearIndex());  // 이전 기록 없으면 현재와 동일 처리

                    return FearIndexConverter.toFearIndexResDTO(currentResult.getFearIndex(), prevScore);
                })
                // [결과 데이터가 없는 경우]
                .orElseGet(() -> FearIndexResDTO.builder()
                        .fearIndex(0)
                        .changeDirection(ChangeDirection.SAME)
                        .changeValue(0)
                        .phrase("분석을 위해 데이터를 모으는 중이에요")
                        .build());
    }
}
