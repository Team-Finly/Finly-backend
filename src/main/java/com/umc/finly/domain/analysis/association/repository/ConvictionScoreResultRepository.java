package com.umc.finly.domain.analysis.association.repository;

import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ConvictionScoreResultRepository extends JpaRepository<ConvictionScoreResult, Long> {

    // 특정 사용자의 가장 최신 분석 결과(최신 7일치) 조회
    Optional<ConvictionScoreResult> findFirstByMemberIdOrderByEndDateDesc(Long memberId);

    // 스케줄러 중복 저장 방지용
    Optional<ConvictionScoreResult> findByMemberIdAndStartDateAndEndDate(Long memberId, LocalDate startDate, LocalDate endDate);
}
