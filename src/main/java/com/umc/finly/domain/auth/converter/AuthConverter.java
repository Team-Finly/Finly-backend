package com.umc.finly.domain.auth.converter;

import com.umc.finly.domain.auth.dto.response.*;
import com.umc.finly.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConverter {

    /** 회원가입 응답 */
    public static AuthSignUpResDTO toSignUpResDTO(Member member, Long personaId) {
        return new AuthSignUpResDTO(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                personaId
        );
    }

    /** 로그인 응답 */
    public static AuthLoginResDTO toLoginResDTO(String accessToken, Member member) {
        return new AuthLoginResDTO(
                accessToken,
                new AuthLoginResDTO.MemberInfo(
                        member.getId(),
                        member.getEmail(),
                        member.getNickname()
                )
        );
    }
}