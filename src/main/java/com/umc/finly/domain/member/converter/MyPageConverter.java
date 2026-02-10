package com.umc.finly.domain.member.converter;

import com.umc.finly.domain.member.dto.response.*;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.mapping.MemberPersonaResults;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageConverter {

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

    public static MyPagePersonaResDTO toMyPagePersonaResDTO(
            MemberPersonaResults result
    ) {
        return new MyPagePersonaResDTO(
                result.getPersona().getPersonaType().toUiType(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }

    public static MyPageMeResDTO toMyPageMeResDTO(Member member) {
        return new MyPageMeResDTO(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }

    public static UpdateNicknameResDTO toUpdateNicknameResDTO(String nickname) {
        return new UpdateNicknameResDTO(nickname);
    }

    public static ProfileImageResDTO toProfileImageResDTO(String imageUrl) {
        return new ProfileImageResDTO(imageUrl);
    }
}