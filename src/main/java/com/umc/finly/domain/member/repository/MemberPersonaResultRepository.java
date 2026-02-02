package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberPersonaResultRepository extends JpaRepository<MembersPersonasResult, Long> {

    // 회원의 페르소나 결과 1건 조회
    // retest 시 업데이트
    Optional<MembersPersonasResult> findByMemberId(Long memId);

    // 마이페이지 페르소나 조회 API
    @Query("""
        select mpr
        from MembersPersonasResult mpr
        join fetch mpr.persona p
        where mpr.memberId = :memberId
""")
    Optional<MembersPersonasResult> findByMemberIdFetchPersona(@Param("memberId") Long memberId);
}
