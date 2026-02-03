package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.MyPageMeRes;
import com.umc.finly.domain.member.dto.response.MyPagePersonaRes;
import com.umc.finly.domain.member.dto.response.UpdateNicknameRes;

public interface MyPageService {
    // 내 페르소나 조회
    MyPagePersonaRes getMyPersona(Long memberId);
    // 내 프로필 정보 조회
    MyPageMeRes getMyInfo(Long memberId);
    // 내 닉네임 변경
    UpdateNicknameRes updateMyNickname(Long memberId, String nickname);
}
