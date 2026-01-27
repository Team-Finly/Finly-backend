package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberPersonaResultRepository extends JpaRepository<MembersPersonasResult, Long> {

    // 회원의 페르소나 결과 1건 조회
    // retest 시 업데이트
    // result 조회 API 에서도 사용할 예정
    Optional<MembersPersonasResult> findByMemberId(Long memId);
}
