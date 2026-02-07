package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.res.HomeMindDetailResDTO;
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

    @GetMapping("/mind")//금융 마음 지수 조회 API
    public ApiResponse<HomeMindResDTO> getHomeMind(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        HomeMindResDTO result =
                homeMindService.getHomeMind(principal.getMemberId());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }


    @Operation(
            summary = "금융 마음 지수 상세 조회 API",
            description = """
                    사용자의 금융 마음 지수 상세 분석 결과를 조회합니다.
                    하락장 회복 탄력성, 의사결정 일치도, 기록 성실도에 대한
                    점수와 해석 문구를 함께 제공합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "금융 마음 지수 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "금융 마음 지수 상세 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "COMMON200",
                                              "message": "성공적으로 요청을 처리했습니다.",
                                              "result": {
                                                "memberName": "키드",
                                                "personaTitle": "신중한 거북이",
                                                "personaDescription": "변동성 속에서도 천천히 판단하는 투자자",
                                                "fmiScore": 64,
                                                "fmiLevel": "평균적 관리",
                                                "fmiComment": "일부 상황에서는 이성적으로 대응하고 있어요",
                                                "scores": {
                                                  "downMarketResilience": {
                                                    "score": 58,
                                                    "description": "코스피가 -1% 이상 하락했을 때 부정적인 감정 조각을 남긴 비율이 다소 높습니다. 변동성 속에서 멘탈을 다듬는 연습이 필요합니다."
                                                  },
                                                  "decisionConsistency": {
                                                    "score": 72,
                                                    "description": "‘확신’ 상태에서 내린 투자 판단이 5영업일 후 실제 시장 결과와 꽤 높은 확률로 일치하고 있습니다."
                                                  },
                                                  "recordConsistency": {
                                                    "score": 65,
                                                    "description": "이번 달 영업일 중 65%의 날에 감정을 기록했습니다. 자기 객관화를 위해 꾸준한 기록 습관이 중요합니다."
                                                  }
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/mind/detail") // 금융 마음 지수 상세 조회 API
    public ApiResponse<HomeMindDetailResDTO> getHomeMindDetail(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        return ApiResponse.onSuccess(
                homeMindService.getHomeMindDetail(principal.getMemberId()),
                SuccessCode.OK
        );
    }

}
