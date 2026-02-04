package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Member m
           set m.refreshToken = :newToken,
               m.refreshTokenExpiredAt = :newExpiredAt
         where m.id = :memberId
           and m.refreshToken = :oldToken
    """)
    int rotateRefreshToken(
            @Param("memberId") Long memberId,
            @Param("oldToken") String oldToken,
            @Param("newToken") String newToken,
            @Param("newExpiredAt") LocalDateTime newExpiredAt
    );
}