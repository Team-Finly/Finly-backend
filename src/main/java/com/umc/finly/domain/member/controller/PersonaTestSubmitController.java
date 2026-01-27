package com.umc.finly.domain.member.controller;

import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReq;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitRes;
import com.umc.finly.domain.member.service.PersonaTestSubmitService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/persona-test")
public class PersonaTestSubmitController {

    private final PersonaTestSubmitService submitService;

    @PostMapping("/submit")
    public ApiResponse<PersonaTestSubmitRes> submit(
            @RequestParam("mode") String mode,
            @RequestBody PersonaTestSubmitReq request
    ){
        return ApiResponse.onSuccess(
                submitService.submit(mode, request),
                SuccessCode.OK
        );
    }
}
