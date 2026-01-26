package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.member.dto.response.PersonaTestQuestionRes;
import com.umc.finly.domain.member.service.PersonaTestService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/persona-test")
public class PersonaTestController {

    private final PersonaTestService personasTestService;

    @GetMapping("/questions")
    public ApiResponse<QuestionListResponse> getPersonasTestQuestions(){
        List<PersonaTestQuestionRes> questions =
                personasTestService.getPersonasTestQuestions();

        return ApiResponse.onSuccess(
                new QuestionListResponse(questions),
                SuccessCode.OK
        );
    }

    private record QuestionListResponse(
            List<PersonaTestQuestionRes> questions
    ){}
}
