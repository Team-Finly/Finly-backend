package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.mapping.MemberPersonaResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberPersonaResultsRepository extends JpaRepository<MemberPersonaResults, Long> {

    // 회원의 페르소나 결과 1건 조회
    // retest 시 업데이트
    Optional<MemberPersonaResults> findByMemberId(Long memId);

    // 마이페이지 페르소나 조회 API
    @Query("""
        select mpr
        from MemberPersonaResults mpr
        join fetch mpr.persona p
        where mpr.memberId = :memberId
""")
    Optional<MemberPersonaResults> findByMemberIdFetchPersona(@Param("memberId") Long memberId);
}
