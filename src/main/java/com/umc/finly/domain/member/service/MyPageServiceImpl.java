package com.umc.finly.domain.member.service;


import com.umc.finly.domain.auth.exception.code.AuthErrorCode;
import com.umc.finly.domain.member.converter.MyPageConverter;
import com.umc.finly.domain.member.dto.response.*;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.mapping.MemberPersonaResults;
import com.umc.finly.domain.member.exception.code.MemberErrorCode;
import com.umc.finly.domain.member.repository.MemberPersonaResultsRepository;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.record.repository.FragmentRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.infra.image.ImageStorageService;
import com.umc.finly.global.security.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MemberPersonaResultsRepository memberPersonaResultsRepository;
    private final MemberRepository memberRepository;
    private final FragmentRepository fragmentRepository;

    private final ImageStorageService imageStorageService;
    private final PasswordEncoder passwordEncoder;

    /** 마이페이지 조회 */
    @Override
    public MyPageResDTO getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        MemberPersonaResults result = memberPersonaResultsRepository.findByMemberIdFetchPersona(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));

        long mindPieceCount = fragmentRepository.countByMemberId(memberId);

        return MyPageConverter.toMyPageResDTO(member, result, mindPieceCount);
    }

    /** 내 페르소나 조회 */
    @Override
    public MyPagePersonaResDTO getMyPersona(Long memberId) {
        MemberPersonaResults result = memberPersonaResultsRepository.findByMemberIdFetchPersona(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));

        return MyPageConverter.toMyPagePersonaResDTO(result);
    }

    /** 내 프로필 조회 */
    @Override
    public MyPageMeResDTO getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MyPageConverter.toMyPageMeResDTO(member);
    }

    /** 내 닉네임 변경 */
    @Override
    @Transactional
    public UpdateNicknameResDTO updateMyNickname(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getNickname().equals(nickname)) {
            return MyPageConverter.toUpdateNicknameResDTO(member.getNickname());
        }

        member.changeNickname(nickname);
        return MyPageConverter.toUpdateNicknameResDTO(member.getNickname());
    }

    /** 프로필 사진 추가 */
    @Override
    @Transactional
    public ProfileImageResDTO addProfileImage(Long memberId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.hasProfileImage()) {
            throw new CustomException(MemberErrorCode.PROFILE_IMAGE_ALREADY_EXISTS);
        }

        String uploadedUrl = null;
        try {
            uploadedUrl = imageStorageService.upload(image, "profile"); // 1) 업로드
            member.setProfileImage(uploadedUrl);                       // 2) DB 반영
            return MyPageConverter.toProfileImageResDTO(uploadedUrl);
        } catch (Exception e) {
            // 3) 보상: 업로드는 됐는데 중간에 터지면 파일 지움
            if (uploadedUrl != null && !uploadedUrl.isBlank()) {
                try {
                    imageStorageService.delete(uploadedUrl);
                } catch (Exception deleteEx) {
                    log.warn("[addProfileImage] rollback delete failed. url={}", uploadedUrl, deleteEx);
                }
            }
            throw e; // 글로벌 핸들러가 처리
        }
    }

    /** 프로필 사진 변경 */
    @Override
    @Transactional
    public ProfileImageResDTO updateProfileImage(Long memberId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String oldImageUrl = member.getProfileImageUrl();
        String newImageUrl = null;

        try {
            newImageUrl = imageStorageService.upload(image, "profile"); // 1) 새 업로드
            member.setProfileImage(newImageUrl);                        // 2) DB 반영
        } catch (Exception e) {
            // 보상: 새 업로드 됐는데 터지면 새 파일 지움
            if (newImageUrl != null && !newImageUrl.isBlank()) {
                try {
                    imageStorageService.delete(newImageUrl);
                } catch (Exception deleteEx) {
                    log.warn("[updateProfileImage] rollback delete failed. newUrl={}", newImageUrl, deleteEx);
                }
            }
            throw e;
        }

        // 3) 이전 파일 삭제
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            try {
                imageStorageService.delete(oldImageUrl);
            } catch (Exception e) {
                log.warn("[updateProfileImage] old image delete failed. oldUrl={}", oldImageUrl, e);
            }
        }

        return MyPageConverter.toProfileImageResDTO(newImageUrl);
    }

    /** 프로필 사진 삭제 */
    @Override
    @Transactional
    public void deleteProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!member.hasProfileImage()) {
            throw new CustomException(MemberErrorCode.PROFILE_IMAGE_NOT_FOUND);
        }

        String oldImageUrl = member.getProfileImageUrl();

        // 1) DB 먼저 null 처리
        member.clearProfileImage();

        // 2) 파일 삭제 실패해도 탈락시키지 않고 로그
        try {
            imageStorageService.delete(oldImageUrl);
        } catch (Exception e) {
            log.warn("[deleteProfileImage] image delete failed. oldUrl={}", oldImageUrl, e);
        }
    }

    /** 내 비밀번호 변경 */
    @Override
    @Transactional
    public void changePassword(Long memberId, String newPassword, String newPasswordConfirm) {
        if (newPassword == null || !newPassword.equals(newPasswordConfirm)) {
            throw new CustomException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        if (!PasswordPolicy.isValid(newPassword)) {
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.changePassword(passwordEncoder.encode(newPassword));
    }

    /** 회원 탈퇴 */
    @Override
    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 1) 프로필 이미지 파일 삭제(실패해도 탈퇴 자체는 진행)
        if (member.hasProfileImage()) {
            String oldImageUrl = member.getProfileImageUrl();
            member.clearProfileImage();

            try {
                imageStorageService.delete(oldImageUrl);
            } catch (Exception e) {
                log.warn("[withdraw] profile image delete failed. oldUrl={}", oldImageUrl, e);
            }
        }

        // 2) refresh token 제거
        member.clearRefreshToken();

        // 3) 회원 삭제
        member.softDelete();
    }
}