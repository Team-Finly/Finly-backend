package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.res.HomeMindResDTO;
import com.umc.finly.domain.home.service.HomeMindService;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.apiPayload.response.SuccessCode;
import com.umc.finly.global.config.security.AuthPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Tag(name = "Home", description = "금융 마음 지수 조회 API")
public class HomeMindController {

    private final HomeMindService homeMindService;

    @Operation(
            summary = "금융 마음 지수 조회 API ",
            description = """
                    홈 화면에 표시되는 사용자의 금융 마음 지수 정보를 조회합니다.
                    """
    )

    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "금융 마음 지수 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "지수 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "COMMON200",
                                              "message": "요청에 성공했습니다.",
                                              "result": {
                                                "userName": "키르",
                                                "personaTitle": "신중한 거북이",
                                                "fmiScore": 64,
                                                "fmiLevelLabel": "평균적 대응",
                                                "fmiDescription": "일부 상황에서는 이성적으로 대응하고 있어요",
                                                "resilienceScore": 70,
                                                "convictionScore": 55,
                                                "consistencyScore": 62
                                              }
                                            }
                                            
                                            """
                            )
                    )
            )
    })

    @GetMapping("/mind")
    public ApiResponse<HomeMindResDTO> getHomeMind(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeMindResDTO result =
                homeMindService.getHomeMind(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

}
