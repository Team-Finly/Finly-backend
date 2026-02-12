package com.umc.finly.domain.member.converter;

import com.umc.finly.domain.member.dto.response.*;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.mapping.MemberPersonaResults;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageConverter {

    /** 마이페이지 상단 조회 **/
    public static MyPageResDTO toMyPageResDTO(
            Member member,
            MemberPersonaResults result,
            long mindPieceCount
    ) {
        return new MyPageResDTO(
                member.getId(),
                member.getNickname(),
                result.getPersona().getPersonaType().toUiType(),
                member.getFinMindIdx(),
                mindPieceCount,
                member.getProfileImageUrl()
        );
    }

    /** 마이 페이지 - 내 페르소나 조회 **/
    public static MyPagePersonaResDTO toMyPagePersonaResDTO(
            MemberPersonaResults result
    ) {
        return new MyPagePersonaResDTO(
                result.getPersona().getPersonaType().toUiType(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }

    /** 마이 페이지 - 내 정보 조회 **/
    public static MyPageMeResDTO toMyPageMeResDTO(Member member) {
        return new MyPageMeResDTO(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }

    /** 마이 페이지 - 닉네임 변경 **/
    public static UpdateNicknameResDTO toUpdateNicknameResDTO(String nickname) {
        return new UpdateNicknameResDTO(nickname);
    }

    /** 마이 페이지 - 프로필 이미지 반환 **/
    public static ProfileImageResDTO toProfileImageResDTO(String imageUrl) {
        return new ProfileImageResDTO(imageUrl);
    }
}