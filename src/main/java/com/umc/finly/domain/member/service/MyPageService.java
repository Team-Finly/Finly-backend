package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    // 마이페이지 조회
    MyPageResDTO getMyPage(Long memberId);
    // 내 페르소나 조회
    MyPagePersonaResDTO getMyPersona(Long memberId);
    // 내 프로필 정보 조회
    MyPageMeResDTO getMyInfo(Long memberId);
    // 내 닉네임 변경
    UpdateNicknameResDTO updateMyNickname(Long memberId, String nickname);
    // 프로필 사진 추가
    ProfileImageResDTO addProfileImage(Long memberId, MultipartFile image);
    // 프로필 사진 변경
    ProfileImageResDTO updateProfileImage(Long memberId, MultipartFile image);
    // 내 비밀번호 변경
    void changePassword(Long memberId, String newPassword, String newPasswordConfirm);
}
