package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.auth.exception.code.AuthErrorCode;
import com.umc.finly.domain.member.dto.request.PasswordChangeReqDTO;
import com.umc.finly.domain.member.dto.request.UpdateNicknameReqDTO;
import com.umc.finly.domain.member.dto.response.*;
import com.umc.finly.domain.member.service.MyPageService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Tag(name = "MyPage", description = "마이페이지 · 내 정보 관리 API")
public class MyPageController {

    private final MyPageService myPageService;

    private Long requireMemberId(AuthPrincipal principal) {
        if (principal == null) {
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }
        return principal.getMemberId();
    }

    /** 마이페이지 상단 통합 조회 */
    @Operation(
            summary = "마이페이지 상단 조회",
            description = """
            마이페이지 상단에 필요한 정보를 한 번에 조회합니다.
            
            - JWT 인증이 필요한 API입니다.
            - 닉네임 / 마음지수(finMindIdx) / 조각수(mindPieceCount) / 현재 페르소나(UI용 한글명) 반환
            """
    )
    @GetMapping
    public ApiResponse<MyPageResDTO> getMyPageTop(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Long memberId = requireMemberId(principal);
        return ApiResponse.onSuccess(
                myPageService.getMyPage(memberId),
                SuccessCode.OK
        );
    }

    /** 내 페르소나 조회 */
    @Operation(
            summary = "내 페르소나 조회",
            description = """
            로그인한 사용자의 페르소나 정보를 조회합니다.
            
            - JWT 인증이 필요한 API입니다.
            - 회원이 최근에 확정된 페르소나 결과를 반환합니다.
            """
    )
    @GetMapping("/persona")
    public ApiResponse<MyPagePersonaResDTO> getMyPersona(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        Long memberId = requireMemberId(principal);
        return ApiResponse.onSuccess(
                myPageService.getMyPersona(memberId),
                SuccessCode.OK
        );
    }

    /** 내 프로필 조회 */
    @Operation(
            summary = "내 프로필 조회",
            description = """
            로그인한 사용자의 기본 프로필 정보를 조회합니다.
            
            - JWT 인증이 필요한 API입니다.
            - 닉네임, 이메일 등 마이페이지에 표시될 기본 정보를 반환합니다.
            """
    )
    @GetMapping("/me")
    public ApiResponse<MyPageMeResDTO> getMyInfo(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        Long memberId = requireMemberId(principal);
        return ApiResponse.onSuccess(
                myPageService.getMyInfo(memberId),
                SuccessCode.OK
        );
    }

    /** 내 닉네임 변경 */
    @Operation(
            summary = "내 닉네임 변경",
            description = """
            로그인한 사용자의 닉네임을 변경합니다.
            
            - JWT 인증이 필요한 API입니다.
            - 요청 바디에 새로운 닉네임을 전달합니다.
            - 닉네임 변경이 성공하면 변경된 닉네임을 반환합니다.
            """
    )
    @PatchMapping("/me/nickname")
    public ApiResponse<UpdateNicknameResDTO> updateNickname(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody @Valid UpdateNicknameReqDTO request
    ){
        Long memberId = requireMemberId(principal);
        return ApiResponse.onSuccess(
                myPageService.updateMyNickname(memberId, request.getNickname()),
                SuccessCode.OK
        );
    }

    /** 프로필 사진 추가 */
    @Operation(
            summary = "프로필 사진 추가",
            description = """
            프로필 사진이 없는 사용자가 새 프로필 이미지를 등록합니다.
            
            - JWT 인증이 필요한 API입니다.
            - multipart/form-data 형식으로 이미지를 업로드합니다.
            - 이미 프로필 사진이 있는 경우 에러를 반환합니다.
            """
    )
    @PostMapping(value = "/me/profile-image", consumes = "multipart/form-data")
    public ApiResponse<ProfileImageResDTO> addProfileImage(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestPart("image") MultipartFile image
    ) {
        Long memberId = requireMemberId(principal);
        return ApiResponse.onSuccess(
                myPageService.addProfileImage(memberId, image),
                SuccessCode.CREATED
        );
    }

    /** 프로필 사진 변경 */
    @Operation(
            summary = "프로필 사진 변경",
            description = """
            사용자의 프로필 이미지를 교체합니다. (없으면 새로 등록)
            
            - JWT 인증이 필요한 API입니다.
            - multipart/form-data 형식으로 이미지를 업로드합니다.
            - 기존 프로필 이미지가 있으면 새 이미지 저장 후 기존 파일을 삭제합니다.
            """
    )
    @PutMapping(value = "/me/profile-image", consumes = "multipart/form-data")
    public ApiResponse<ProfileImageResDTO> updateProfileImage(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestPart("image") MultipartFile image
    ) {
        Long memberId = requireMemberId(principal);
        return ApiResponse.onSuccess(
                myPageService.updateProfileImage(memberId, image),
                SuccessCode.OK
        );
    }

    /** 프로필 사진 삭제 */
    @Operation(
            summary = "프로필 사진 삭제",
            description = """
            사용자의 프로필 이미지를 삭제합니다.
            
            - JWT 인증이 필요한 API입니다.
            - 프로필 이미지가 있으면 파일 삭제 + DB URL null 처리합니다.
            - 프로필 이미지가 없는 경우 404 에러를 반환합니다.
            """
    )
    @DeleteMapping("/me/profile-image")
    public ApiResponse<Map<String, Object>> deleteProfileImage(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        Long memberId = requireMemberId(principal);
        myPageService.deleteProfileImage(memberId);
        return ApiResponse.onSuccess(Map.of(), SuccessCode.OK);
    }

    /** 비밀번호 변경 */
    @Operation(
            summary = "비밀번호 변경",
            description = """
            로그인한 사용자의 비밀번호를 변경합니다.
            
            - Authorization: Bearer {accessToken} 필요
            - newPassword와 newPasswordConfirm이 일치해야 합니다.
            - 비밀번호 정책을 만족해야 합니다.
            """
    )
    @PatchMapping("/me/password")
    public ApiResponse<Map<String, Object>> changePassword(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody PasswordChangeReqDTO request
    ){
        Long memberId = requireMemberId(principal);
        myPageService.changePassword(memberId, request.getNewPassword(), request.getNewPasswordConfirm());
        return ApiResponse.onSuccess(Map.of(), SuccessCode.OK);
    }

    /** 회원 탈퇴 */
    @Operation(
            summary = "회원 탈퇴",
            description = """
            로그인한 사용자가 회원 탈퇴를 진행합니다.
            
            - JWT 인증이 필요한 API입니다.
            - 회원 정보는 논리 삭제 처리됩니다.
            - 리프레시 토큰은 함께 정리됩니다.
            """
    )
    @DeleteMapping("/me") // ✅ 기존 "/members/me"가 필요하면 이 줄만 원복
    public ApiResponse<Map<String, Object>> withdraw(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        Long memberId = requireMemberId(principal);
        myPageService.withdraw(memberId);
        return ApiResponse.onSuccess(Map.of(), SuccessCode.OK);
    }
}