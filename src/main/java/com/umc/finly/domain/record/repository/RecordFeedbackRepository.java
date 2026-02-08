package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordFeedback;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// AI 피드백(RecordFeedback) 엔티티의 데이터 접근 레이어
// 피드백 조회 및 동시성 제어를 담당함
public interface RecordFeedbackRepository extends JpaRepository<RecordFeedback, Long> {

    // 기록 ID로 피드백 조회
    Optional<RecordFeedback> findByRecordEntryId(Long recordEntryId);

    // 기록 ID + 회원 ID로 피드백 조회 (권한 검사 포함)
    Optional<RecordFeedback> findByRecordEntryIdAndMemberId(Long recordEntryId, Long memberId);

    // 비관적 락으로 피드백 조회 (동시 재생성 요청 경쟁 조건 방지)
    // PESSIMISTIC_WRITE: 다른 트랜잭션의 읽기/쓰기 모두 차단
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM RecordFeedback f WHERE f.recordEntryId = :recordEntryId")
    Optional<RecordFeedback> findByRecordEntryIdForUpdate(@Param("recordEntryId") Long recordEntryId);
}
