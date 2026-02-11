package com.umc.finly.domain.home.controller;

import com.umc.finly.domain.home.dto.response.HomeMindDetailResDTO;
import com.umc.finly.domain.home.dto.response.HomeMindResDTO;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface HomeMindApiSpecification {

    @Operation(
            summary = "금융 마음 지수 조회",
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
                                    name = "금융 마음 지수 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "COMMON200",
                                              "message": "요청에 성공했습니다.",
                                              "result": {
                                                "fmiComment": "시장의 흐름보다 감정의 영향을 더 많이 받고 있습니다.",
                                                "fmiLevel": "감정 영향 높음",
                                                "fmiScore": 8,
                                                "memberName": "핀리",
                                                "persona": {
                                                  "personaTitle": "신중한 거북이",
                                                  "personaType": "CAUTIOUS_TURTLE"
                                                }
                                              }
                                            }
                                           """
                            )
                    )
            )
    })
    public ApiResponse<HomeMindResDTO> getHomeMind(
            @AuthenticationPrincipal AuthPrincipal principal
    );

    @Operation(
            summary = "금융 마음 지수 상세 조회",
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
                                              "message": "요청에 성공했습니다.",
                                              "result": {
                                            	  "fmiScore": 64,
                                                "fmiLevel": "평균적 대응",
                                                "fmiComment": "일부 상황에서는 이성적으로 대응하고 있습니다.",
                                                "memberName": "키르",
                                                "persona": {
                                                  "personaType": "과감한 면이 있어도, 원칙(안정)이 우선하는 성향이에요!",
                                                  "personaTitle": "string"
                                                },
                                                "scores": {
                                                  "downMarketResilience": {
                                                    "score": 58,
                                                    "description": "확신 상태에서 내린 판단과 실제 시장 결과의 괴리가 큽니다."
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
    public ApiResponse<HomeMindDetailResDTO> getHomeMindDetail(
            @AuthenticationPrincipal AuthPrincipal principal
    );
}
