package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.MyPageMeResDTO;
import com.umc.finly.domain.member.dto.response.MyPagePersonaResDTO;
import com.umc.finly.domain.member.dto.response.UpdateNicknameResDTO;

public interface MyPageService {
    // 내 페르소나 조회
    MyPagePersonaResDTO getMyPersona(Long memberId);
    // 내 프로필 정보 조회
    MyPageMeResDTO getMyInfo(Long memberId);
    // 내 닉네임 변경
    UpdateNicknameResDTO updateMyNickname(Long memberId, String nickname);
    // 내 비밀번호 변경
    void changePassword(Long memberId, String newPassword, String newPasswordConfirm);
}
