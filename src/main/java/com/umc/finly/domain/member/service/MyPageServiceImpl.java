package com.umc.finly.domain.member.service;

import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.member.dto.response.MyPageMeResDTO;
import com.umc.finly.domain.member.dto.response.MyPagePersonaResDTO;
import com.umc.finly.domain.member.dto.response.ProfileImageResDTO;
import com.umc.finly.domain.member.dto.response.UpdateNicknameResDTO;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.infra.image.ImageStorageService;
import com.umc.finly.global.util.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService{
    // MyPage 관련 서비스

    private final MemberPersonaResultRepository memberPersonaResultRepository;
    private final MemberRepository memberRepository;

    private final ImageStorageService imageStorageService;
    private final PasswordEncoder passwordEncoder;

    // 내 페르소나 조회
    @Override
    public MyPagePersonaResDTO getMyPersona(Long memberId){
        return memberPersonaResultRepository.findByMemberIdFetchPersona(memberId)
                .map(MyPagePersonaResDTO::from)
                .orElseThrow(()-> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));
    }

    // 내 프로필 조회
    @Override
    public MyPageMeResDTO getMyInfo(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MyPageMeResDTO.from(member);
    }

    // 내 닉네임 변경
    @Override
    @Transactional
    public UpdateNicknameResDTO updateMyNickname(Long memberId, String nickname){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 변경 없을 시
        if (member.getNickname().equals(nickname)){
            return UpdateNicknameResDTO.of(member.getNickname());
        }

        member.changeNickname(nickname);

        return UpdateNicknameResDTO.of(member.getNickname());
    }

    // 프로필 사진 추가
    @Override
    @Transactional
    public ProfileImageResDTO addProfileImage(Long memberId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 이미 프로필 사진이 있는 경우
        if (member.hasProfileImage()) {
            throw new CustomException(MemberErrorCode.PROFILE_IMAGE_ALREADY_EXISTS);
        }

        // 이미지 업로드
        String imageUrl = imageStorageService.upload(image, "profile");

        // URL 저장
        member.addProfileImage(imageUrl);
        memberRepository.save(member);

        return ProfileImageResDTO.builder()
                .profileImageUrl(imageUrl)
                .build();
    }

    // 프로필 사진 변경
    @Override
    @Transactional
    public ProfileImageResDTO updateProfileImage(Long memberId, MultipartFile image){
        if (image == null || image.isEmpty()) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 이미 프로필 사진이 있는 경우
        String oldImageUrl = member.getProfileImageUrl();

        // 새 이미지 업로드
        String newImageUrl = imageStorageService.upload(image, "profile");

        // DB에 새 URL 저장
        // (member.addProfileImage가 "없을 때만" 동작하는 메서드면, 교체용 setter/update 메서드가 필요)
        member.updateProfileImage(newImageUrl);
        memberRepository.save(member);

        // 기존 파일 삭제 (새 업로드/DB 저장 성공 후)
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            imageStorageService.delete(oldImageUrl);
        }

        return ProfileImageResDTO.builder()
                .profileImageUrl(newImageUrl)
                .build();
    }

    // 내 비밀번호 변경
    @Override
    @Transactional
    public void changePassword(Long memberId, String newPassword, String newPasswordConfirm){

        // 1) confirm 검증
        if (newPassword == null || !newPassword.equals(newPasswordConfirm)){
            throw new CustomException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        // 2) 정책 검증
        if (!PasswordPolicy.isValid(newPassword)){
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }

        // 3) Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 4) 저장
        member.changePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }
}
