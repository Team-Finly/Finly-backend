package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.member.dto.request.UpdateNicknameReqDTO;
import com.umc.finly.domain.member.dto.response.MyPageMeResDTO;
import com.umc.finly.domain.member.dto.response.MyPagePersonaResDTO;
import com.umc.finly.domain.member.dto.response.UpdateNicknameResDTO;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Tag(name = "MyPage", description = "마이페이지 · 내 정보 관리")
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 내 페르소나 조회
     */
    @Operation(
            summary = "내 페르소나 조회",
            description = """
                로그인한 사용자의 페르소나 정보를 조회합니다.
                
                - JWT 인증이 필요한 API입니다.
                - 회원이 최근에 확정된 페르소나 결과를 반환합니다.
                - 인증되지 않은 경우 UNAUTHORIZED 에러를 반환합니다.
                """
    )
    @GetMapping("/persona")
    public ApiResponse<MyPagePersonaResDTO> getMyPersona(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        if(principal == null){
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }
        return ApiResponse.onSuccess(
                myPageService.getMyPersona(principal.getMemberId()),
                SuccessCode.OK
        );
    }

    /**
     * 내 프로필 조회
     */
    @Operation(
            summary = "내 프로필 조회",
            description = """
                로그인한 사용자의 기본 프로필 정보를 조회합니다.
                
                - JWT 인증이 필요한 API입니다.
                - 닉네임, 이메일 등 마이페이지에 표시될 기본 정보를 반환합니다.
                - 인증되지 않은 경우 UNAUTHORIZED 에러를 반환합니다.
                """
    )
    @GetMapping("/me")
    public ApiResponse<MyPageMeResDTO> getMyInfo(
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        if (principal == null){
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.onSuccess(
                myPageService.getMyInfo(principal.getMemberId()),
                SuccessCode.OK
        );
    }

    /**
     * 내 닉네임 변경
     */
    @Operation(
            summary = "내 닉네임 변경",
            description = """
                로그인한 사용자의 닉네임을 변경합니다.
                
                - JWT 인증이 필요한 API입니다.
                - 요청 바디에 새로운 닉네임을 전달합니다.
                - 닉네임 변경이 성공하면 변경된 닉네임을 반환합니다.
                - 인증되지 않은 경우 UNAUTHORIZED 에러를 반환합니다.
                """
    )
    @PostMapping("/me/nickname")
    public ApiResponse<UpdateNicknameResDTO> updateNickname(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody @Valid UpdateNicknameReqDTO request
            ){

        if (principal == null){
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.onSuccess(
                myPageService.updateMyNickname(
                        principal.getMemberId(),
                        request.getNickname()
                ),
                SuccessCode.OK
        );
    }
}
