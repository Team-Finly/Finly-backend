package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.member.dto.response.PersonasTestQuestionRes;
import com.umc.finly.domain.member.service.PersonasTestService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 페르소나 테스트 관련 비즈니스 로직 서비스
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/persona-test")
public class PersonasTestController {

    private final PersonasTestService personasTestService;

    // 페르소나 테스트 질문 목록 조회 API
    /** 회원가입/재테스트 화면에 사용됨 **/
    @GetMapping("/questions")
    public ApiResponse<QuestionListResponse> getPersonasTestQuestions(){
        List<PersonasTestQuestionRes> questions =
                personasTestService.getPersonasTestQuestions();

        return ApiResponse.onSuccess(
                new QuestionListResponse(questions),
                SuccessCode.OK
        );
    }

    // questions 리스트 wrapper 응답 DTO
    private record QuestionListResponse(
            List<PersonasTestQuestionRes> questions
    ){}
}
