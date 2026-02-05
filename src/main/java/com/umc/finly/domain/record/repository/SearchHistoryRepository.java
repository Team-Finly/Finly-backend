package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    /**
     * 해당 회원의 동일 키워드 검색 기록 조회
     */
    Optional<SearchHistory> findByMemberIdAndKeyword(Long memberId, String keyword);

    /**
     * 해당 회원의 최근 검색 키워드 조회 (최신순, 상위 N개)
     */
    @Query("""
            SELECT sh.keyword FROM SearchHistory sh
            WHERE sh.memberId = :memberId
            ORDER BY sh.updatedAt DESC
            """)
    List<String> findRecentKeywordsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * updatedAt 명시적 갱신 (touch)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SearchHistory sh SET sh.updatedAt = CURRENT_TIMESTAMP WHERE sh.id = :id")
    int touchUpdatedAt(@Param("id") Long id);
}
