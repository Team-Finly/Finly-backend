package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecordEntryRepository extends JpaRepository<RecordEntry, Long> {
    boolean existsByClientRequestId(String clientRequestId);
    List<RecordEntry> findByMemberIdOrderByRecordDateDesc(Long memberId, Pageable pageable);
    List<RecordEntry> findByMemberIdAndRecordDateOrderByCreatedAtAsc(Long memberId, LocalDate recordDate);

    @Query("SELECT r FROM RecordEntry r WHERE r.memberId = :memberId" +
            " AND (:emotionCode IS NULL OR r.emotionCode = :emotionCode)" +
            " AND (:keyword IS NULL OR r.memo LIKE CONCAT('%', :keyword, '%')" +
            "      OR (:stockIds IS NOT NULL AND r.stockId IN :stockIds))" +
            " ORDER BY r.createdAt DESC")
    List<RecordEntry> searchRecords(
            @Param("memberId") Long memberId,
            @Param("emotionCode") EmotionCode emotionCode,
            @Param("keyword") String keyword,
            @Param("stockIds") List<Long> stockIds);

    List<RecordEntry> findAllByMemberIdAndStockId(Long memberId, Long stockId);

    // fragment
    long countByMemberId(Long memberId); // 해당 회원이 기록한 전체 기록 개수

    // 감정 타입별 개수 조회 결과를 받기 위한 Projection
    interface EmotionCountProjection {
        EmotionCode getEmotionCode();
        Long getCount();
    }

    // 회원의 기록을 감정 타입(emotionCode) 기준으로 그룹핑하여 개수 집계
    @Query("""
        select r.emotionCode as emotionCode, count(r) as count
        from RecordEntry r
        where r.memberId = :memberId
        group by r.emotionCode
    """)
    List<EmotionCountProjection> countGroupByEmotionCode(@Param("memberId") Long memberId);
}
