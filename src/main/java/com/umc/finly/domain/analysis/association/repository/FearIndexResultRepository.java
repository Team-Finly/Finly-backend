package com.umc.finly.domain.analysis.association.repository;

import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FearIndexResultRepository extends JpaRepository<FearIndexResult, Long> {

    // 특정 사용자의 가장 최신 분석 결과(최신 7일치) 조회
    Optional<FearIndexResult> findFirstByMemberIdOrderByEndDateDesc(Long memberId);

    // 변화량 비교용: 현재 결과의 시작일보다 이전에 끝난 가장 최근 결과 조회
    Optional<FearIndexResult> findFirstByMemberIdAndEndDateBeforeOrderByEndDateDesc(Long memberId, LocalDate startDate);

    // 스케줄러 중복 저장 방지용 (특정 기간 데이터 존재 여부 확인)
    Optional<FearIndexResult> findByMemberIdAndStartDateAndEndDate(Long memberId, LocalDate startDate, LocalDate endDate);
}
