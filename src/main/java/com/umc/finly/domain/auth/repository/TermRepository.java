package com.umc.finly.domain.auth.repository;

import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.enums.TermType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    // 약관 목록 전체 조회
    List<Term> findAllByOrderByIdAsc();

    // TermType으로 단건 조회
    Optional<Term> findByTermType(TermType termType);
}
