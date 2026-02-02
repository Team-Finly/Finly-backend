package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReq;
import com.umc.finly.domain.member.dto.response.PersonaTestQuestionRes;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitRes;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.service.PersonaTestService;
import com.umc.finly.domain.member.service.PersonaTestSubmitService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 페르소나 테스트 관련 비즈니스 로직 서비스
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/persona-test")
public class PersonasTestController {

    private final PersonaTestService personasTestService;
    private final PersonaTestSubmitService submitService;

    /** 페르소나 테스트 질문 목록 조회 API **/
    @GetMapping("/questions")
    public ApiResponse<QuestionListResponse> getPersonasTestQuestions(){
        List<PersonaTestQuestionRes> questions =
                personasTestService.getPersonasTestQuestions();

        return ApiResponse.onSuccess(
                new QuestionListResponse(questions),
                SuccessCode.OK
        );
    }

    /** 페르소나 테스트 제출 API **/
    @PostMapping("/submit")
    public ApiResponse<PersonaTestSubmitRes> submit(
            @RequestParam("mode") String mode,
            @RequestBody PersonaTestSubmitReq request
    ){
        if (!"signup".equals(mode) && !"retest".equals(mode)) {
            throw new CustomException(MemberErrorCode.INVALID_PERSONA_MODE);
        }
        return ApiResponse.onSuccess(
                submitService.submit(mode, request),
                SuccessCode.OK
        );
    }

    // questions 리스트 wrapper 응답 DTO
    private record QuestionListResponse(
            List<PersonaTestQuestionRes> questions
    ){}
}
