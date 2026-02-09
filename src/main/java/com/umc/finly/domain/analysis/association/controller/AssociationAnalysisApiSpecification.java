package com.umc.finly.domain.analysis.association.controller;

import com.umc.finly.domain.analysis.association.dto.*;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.config.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "AssociationAnalysis", description = "통계 - 연관분석 관련 API")
public interface AssociationAnalysisApiSpecification {

    @Operation(summary = "통계 진입 사용자 상태 조회", description = """
            통계 진입한 사용자의 상태를 기록 개수에 따라 구분합니다.
            - 기록 개수 0개 (recordLevel = NONE, defaultStock = null)
            - 기록 개수 1-2개 (recordLevel = LOW, defaultStock = null)
            - 기록 개수 3개 이상 (recordLevel = HIGH, defaultStock = 가장 많이 기록한 종목 1개의 id, symbol, name)
              - 가장 많이 기록한 종목이 여러 개일 경우, 가장 최근에 기록한 종목 반환
            
            종목에 기반한 통계 관련 API를 호출할 때 API 경로에 symbol을 path variable로 넣습니다.
              - 예) /api/analysis/stocks/{symbol}/summary (통계-주식데이터-종목 분석 조회 API)
              - 최다 기록 종목 자동 노출의 경우 본 API 응답에서 디폴트 종목 symbol 확인
              - 사용자가 직접 종목 선택할 경우 '사용자 기록 종목 조회 API' 응답에서 symbol 확인  
            """)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "인증되지 않음", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "사용자 인증 없이 접근 불가",
                                    summary = "인증 실패",
                                    value = "{ \"isSuccess\": false, \"code\": \"AUTH401_2\", \"message\": \"인증이 필요합니다.\", \"result\": \"인증이 필요합니다.\" }"
                            )
                    })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "최다 기록 종목 찾기 실패(최다 기록 종목 id를 찾지 못했거나, 찾은 id를 가진 종목이 존재하지 않을 때)",
                                    summary = "최다 기록 종목을 찾을 수 없음",
                                    value = "{ \"isSuccess\": false, \"code\": \"ANALYSIS_ENTRY404\", \"message\": \"사용자가 가장 많이 기록한 종목을 찾을 수 없습니다.\" }"
                            )
                    })
            )
    })
    public ApiResponse<AnalysisEntryResDTO> getEntryStatus(AuthPrincipal principal);


    @Operation(
            summary = "사용자가 기록한 종목 목록 조회",
            description = """
                    사용자가 매매 기록을 남긴 적이 있는 종목 목록을 조회합니다.

                    - 동일 종목을 여러 번 기록했더라도 종목당 1회만 반환됩니다.
                    - 사용자 인증 정보(JWT)를 기반으로 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자가 기록한 종목 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "isSuccess": true,
                                              "code": "ANALYSIS200",
                                              "message": "사용자가 기록한 종목 목록을 정상적으로 조회했습니다.",
                                              "result": [
                                                {
                                                  "stockId": 123,
                                                  "symbol": "005930",
                                                  "stockName": "삼성전자"
                                                },
                                                {
                                                  "stockId": 124,
                                                  "symbol": "000660",
                                                  "stockName": "SK하이닉스"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    public ApiResponse<List<AnalysisStockResDTO>> getRecordStocks(AuthPrincipal principal);



    @Operation(summary = "일별 주가 감정 그래프 조회", description = """
    오늘부터 과거 최근 7영업일 동안의 종목별 주가 데이터와 사용자의 기록 데이터를 결합하여 날짜별로 모아 반환합니다.
    - 7영업일 확보 방법: 오늘부터 14일 전까지 데이터 요청 → 데이터가 7개가 안되면 조회 시작일을 더 과거(30일 전)로 밀어서 재요청 → 데이터가 7개 모이면 중단하고 최신순으로 자르기
    - 7영업일 치의 주가 데이터를 확보한 후, 해당 데이터 리스트의 양 끝값(첫 날과 마지막 날)에서 실제 조회된 시작일과 종료일을 추출
    - 주가 데이터 API: 한국투자증권 API [국내주식] 기본시세 - 국내주식기간별시세(일/주/월/년)[v1_국내주식-016] (일봉)
    
    - today: 서버 실행 날짜(프론트에서 API 호출한 날짜)
    - 종목 정보 - 종목ID, symbol(종목 코드), 이름
    - 날짜 - 시작일, 종료일
    - 일별 데이터
        - 주식 영업일자 (날짜, 요일)
        - 주가 데이터 - 해당 일의 해당 종목 종가
        - 사용자 기록 데이터 - 해당 일에 남긴 기록 전체 개수, 기록된 감정들, 메인 감정(횟수 비교 (최다 선택) → 강도 비교 (최대 선택) → 생성 시각 비교 (최신 선택))
    """)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "인증되지 않음", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "사용자 인증 없이 접근 불가",
                                    summary = "인증 실패",
                                    value = "{ \"isSuccess\": false, \"code\": \"AUTH401_2\", \"message\": \"인증이 필요합니다.\", \"result\": \"인증이 필요합니다.\" }"
                            )
                    })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "잘못된 요청", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "종목 코드(symbol) 형식이 잘못됨",
                                    summary = "잘못된 종목 코드",
                                    value = "{ \"isSuccess\": false, \"code\": \"KI400\", \"message\": \"종목 코드 형식이 올바르지 않습니다. 종목 코드는 숫자 6자리 형식이어야 합니다.\" }"
                            )
                    })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "해당 종목 코드를 가진 종목을 찾을 수 없음",
                                    summary = "종목이 존재하지 않음",
                                    value = "{ \"isSuccess\": false, \"code\": \"KI404\", \"message\": \"존재하지 않는 종목입니다.\" }"
                            )
                    })
            )
    })
    public ApiResponse<DailyChartResDTO> getDailyChart(AuthPrincipal principal, String symbol);

    @Operation(summary = "시간별 그래프 조회", description = """
    선택한 날짜에 대한 시간별 주가 감정 그래프를 조회합니다.
    - targetDate 넣지 않을 경우 자동으로 오늘 날짜 지정됩니다.
    - targetDate가 휴장일이거나 장 시작 전일 경우 prices가 빈 배열로 반환됩니다.
    - 장 진행 중일 때 요청할 경우 요청한 현재 시각으로부터 1분 전 price까지 받아옵니다.
    
    - recordDateTime 로직
      - recordDate 날짜 != createdAt 날짜인 경우: recordDate의 날짜 + session에 따른 시각 매핑(PRE_MARKET=9:00, MORNING=10:30, AFTERNOON=13:30, POST_MARKET=15:30)
      - recordDate 날짜 == createdAt 날짜인 경우:
        - createdAt 시각이 9:00~15:30일 때: createdAt의 시각 그대로 사용
        - createdAt 시각이 9:00 이전일 때: 시각 9:00으로 설정
        - createdAt 시각이 15:30 이후일 때: 시각 15:30으로 설정
    """)
    public ApiResponse<HourlyChartResDTO> getHourlyChart(AuthPrincipal principal, String symbol, LocalDate targetDate);

    @Operation(summary = "하락장 공포지수 조회", description = "가장 최근 하락장 공포지수를 조회합니다.")
    public ApiResponse<FearIndexResDTO> getFearIndex(AuthPrincipal principal);

    @Operation(summary = "매수 확신도 조회", description = "가장 최근 매수 확신도를 조회합니다.")
    public ApiResponse<ConvictionScoreResDTO> getConvictionScore(AuthPrincipal principal);

    @Operation(summary = "AI 분석 조회", description = "연관분석 데이터를 기반으로 한 AI 분석 결과를 조회합니다.")
    public ApiResponse<AiAnalysisResDTO> getAiAnalysis(AuthPrincipal principal);
}
