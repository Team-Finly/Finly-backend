package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResDTO {
    private Long memberId;
    private String nickname;
    private PersonaUiType personaType;
    private Integer finMindIdx;
    private Long mindPieceCount;
    private String profileImageUrl;
}
