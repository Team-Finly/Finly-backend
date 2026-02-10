package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.auth.exception.code.AuthErrorCode;
import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReqDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestQuestionResDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitResDTO;
import com.umc.finly.domain.member.enums.PersonaMode;
import com.umc.finly.domain.member.service.PersonaTestService;
import com.umc.finly.domain.member.service.PersonaTestSubmitService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 페르소나 테스트 관련 비즈니스 로직 서비스
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/persona-test")
@Tag(name = "PersonaTest", description = "페르소나 테스트")
public class PersonaTestController {

    private final PersonaTestService personasTestService;
    private final PersonaTestSubmitService submitService;

    /**
     * 페르소나 테스트 질문 목록 조회
     */
    @Operation(
            summary = "페르소나 테스트 질문 목록 조회",
            description = """
                페르소나 테스트에 사용되는 질문과 선택지 목록을 조회합니다.
                
                - 회원가입 전/후 모두 사용 가능한 API입니다.
                - 질문은 고정된 순서로 반환됩니다.
                - 각 질문에는 선택지 목록이 포함됩니다.
                """
    )
    @GetMapping("/questions")
    public ApiResponse<QuestionListResponse> getPersonasTestQuestions(){
        List<PersonaTestQuestionResDTO> questions =
                personasTestService.getPersonasTestQuestions();

        return ApiResponse.onSuccess(
                new QuestionListResponse(questions),
                SuccessCode.OK
        );
    }

    /**
     * 페르소나 테스트 제출
     */
    @Operation(
            summary = "페르소나 테스트 제출",
            description = """
                사용자가 선택한 답안을 제출하여 페르소나 결과를 계산합니다.
                
                - mode 파라미터에 따라 동작 방식이 달라집니다.
                  • signup : 회원가입 전 미리보기용 (저장되지 않음)
                  • retest : 로그인 후 재테스트 (결과 저장)
                
                - retest 모드는 JWT 인증이 필요합니다.
                - 잘못된 mode 값이 전달되면 INVALID_PERSONA_MODE 에러를 반환합니다.
                """
    )
    @PostMapping("/submit")
    public ApiResponse<PersonaTestSubmitResDTO> submit(
            @RequestParam("mode") PersonaMode mode,
            @RequestBody @Valid PersonaTestSubmitReqDTO request,
            @AuthenticationPrincipal AuthPrincipal authPrincipal
            ){
        Long memberId = null;

        if (mode == PersonaMode.retest){
            if (authPrincipal == null) throw new CustomException(AuthErrorCode.UNAUTHORIZED);
            memberId = authPrincipal.getMemberId();
        }

        return ApiResponse.onSuccess(
                submitService.submit(mode.name(), memberId, request),
                SuccessCode.OK
        );
    }

    // questions 리스트 wrapper 응답 DTO
    private record QuestionListResponse(
            List<PersonaTestQuestionResDTO> questions
    ){}
}
