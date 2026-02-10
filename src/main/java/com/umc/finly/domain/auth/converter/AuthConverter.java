package com.umc.finly.domain.auth.converter;

import com.umc.finly.domain.auth.dto.res.*;
import com.umc.finly.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConverter {

    /** 이메일 중복 확인 응답 */
    public static CheckEmailResDTO toCheckEmailResDTO(boolean available) {
        return new CheckEmailResDTO(available);
    }

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

    /** 토큰 재발급 응답 */
    public static AuthReissueResDTO toReissueResDTO(String accessToken) {
        return new AuthReissueResDTO(accessToken);
    }
}