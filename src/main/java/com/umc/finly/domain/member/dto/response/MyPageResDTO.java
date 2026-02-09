package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageResDTO {
    private Long memberId;
    private String nickname;
    private PersonaUiType personaType;
    private Integer finMindIdx;
    private Long mindPieceCount;
    private String profileImageUrl;

    public static MyPageResDTO of(
            Member member,
            MembersPersonasResult result,
            long mindPieceCount
    ){
        return MyPageResDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .personaType(result.getPersona().getPersonaType().toUiType())
                .finMindIdx(member.getFinMindIdx())
                .mindPieceCount(mindPieceCount)
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
