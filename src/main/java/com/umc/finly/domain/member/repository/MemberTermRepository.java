package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.auth.entity.mapping.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
}
