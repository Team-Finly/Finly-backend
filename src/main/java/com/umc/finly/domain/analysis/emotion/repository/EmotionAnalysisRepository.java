package com.umc.finly.domain.analysis.emotion.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmotionAnalysisRepository extends JpaRepository<RecordEntry, Long> {

    // 회원의 기록을 감정 타입(emotionCode) 기준으로 그룹핑하여 개수 집계
    @Query("""
        select r.emotionCode as emotionCode, count(r) as count
        from RecordEntry r
        where r.memberId = :memberId
            and r.stockId = :stockId
        group by r.emotionCode
    """)
    List<EmotionCountProjection> countGroupByEmotionCode(@Param("memberId") Long memberId, @Param("stockId") Long stockId);

    List<RecordEntry> findAllByMemberIdAndStockIdAndDeletedAtIsNull(Long memberId, Long stockId);
    // ===== Projection interfaces =====

    // 감정 타입별 개수 조회 결과를 받기 위한 Projection
    interface EmotionCountProjection {
        EmotionCode getEmotionCode();
        Long getCount();
    }
}