package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.dto.HourlyChartResDTO;
import com.umc.finly.domain.analysis.association.dto.KoreaInvestRawResponse;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestErrorCode;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 한국투자증권 API 실제 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KoreaInvestApiCallerImpl implements DailyChartApiCaller, HourlyChartApiCaller {

    private final RestClient koreaInvestRestClient;

    // [Daily Chart] 국내주식기간별시세(일/주/월/년)[v1_국내주식-016] -> 일봉 조회
    @Override
    public List<Map<String, Object>> fetchDailyCandles(String symbol, String startDate, String endDate) {
        // API 호출
        KoreaInvestRawResponse response = koreaInvestRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // 주식 시장 분류 (J: KRX)
                        .queryParam("FID_INPUT_ISCD", symbol)       // 종목 코드
                        .queryParam("FID_INPUT_DATE_1", startDate)  // 시작일
                        .queryParam("FID_INPUT_DATE_2", endDate)    // 종료일
                        .queryParam("FID_PERIOD_DIV_CODE", "D")    // D: 일봉
                        .queryParam("FID_ORG_ADJ_PRC", "0")         // 0: 수정주가 적용
                        .build())
                .header("tr_id", "FHKST03010100") // 거래 ID
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response1) -> {
                    throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
                })
                .body(KoreaInvestRawResponse.class);

        validateResponse(response, symbol);

        return response.getOutput2();
    }

    // [하락장 공포지수]
    // fetchIndexByMinute

    // [매수 확신도]
    // fetchIndexByDate

    // [Hourly Chart] 주식일별분봉조회
    @Override
    public List<Map<String, Object>> fetchHourlyChart(String symbol, String targetDate) {
        List<Map<String, Object>> allRawData = new ArrayList<>();
        String nextTime = "153000"; // 시작 시간 (장 마감 시각)
        boolean hasNext = true;

        while (hasNext) {
            String finalNextTime = nextTime;
            KoreaInvestRawResponse response = koreaInvestRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/domestic-stock/v1/quotations/inquire-time-dailychartprice")
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", symbol)
                            .queryParam("FID_INPUT_HOUR_1", finalNextTime) // 이전 응답의 마지막 시간
                            .queryParam("FID_INPUT_DATE_1", targetDate)
                            .queryParam("FID_PW_DATA_INCU_YN", "N")
                            .queryParam("FID_FAKE_TICK_INCU_YN", " ")
                            .build())
                    .header("tr_id", "FHKST03010230")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response1) -> {
                        throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
                    })
                    .body(KoreaInvestRawResponse.class);

            if (response == null || response.getOutput2() == null || response.getOutput2().isEmpty()) {
                break;
            }

            List<Map<String, Object>> currentBatch = response.getOutput2();
            allRawData.addAll(currentBatch);

            // 마지막 데이터의 시간을 확인
            Map<String, Object> lastData = currentBatch.get(currentBatch.size() - 1);
            String lastTime = lastData.get("stck_cntg_hour").toString().replaceAll("\"", "");

            // 9시 데이터까지 도달했거나, 더 이상 과거 데이터가 없으면 중단
            // 한투 API는 내림차순으로 주므로 lastTime이 nextTime과 같으면 끝난 것
            if (lastTime.compareTo("090000") <= 0 || lastTime.equals(nextTime)) {
                hasNext = false;
            } else {
                // 마지막 시각의 1분 전부터 다시 조회 (중복 방지)
                int lastTimeInt = Integer.parseInt(lastTime);
                nextTime = String.format("%06d", lastTimeInt);
            }
        }

        // 중복 제거 및 반환
        return allRawData.stream()
                .distinct()
                .collect(Collectors.toList());
    }


    private void validateResponse(KoreaInvestRawResponse response, String symbol) {
        // response가 null인 경우
        if (response == null) {
            log.error("❌ 한투 API 응답 바디가 null입니다. (역직렬화 실패 또는 빈 응답) - 종목코드: {}", symbol);
            throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
        }
        // 한투 응답 에러 (rt_cd != "0") 발생한 경우
        if (!"0".equals(response.getRtCd())) {
            log.error("❌ 한투 API 응답 에러(rt_cd=1) [종목코드: {}]: msg_cd={}, msg1={}",
                    symbol, response.getMsgCd(), response.getMsg1());
            throw new KoreaInvestException(KoreaInvestErrorCode.API_BUSINESS_ERROR);
        }
        // rt_cd는 0이지만 데이터(output2)가 없는 경우
        if (response.getOutput2() == null || response.getOutput2().isEmpty()) {
            log.warn("⚠\uFE0F 한투 API 데이터 유실 [종목코드: {}]: rt_cd는 성공이나 output2가 비어있음.", symbol);
            throw new KoreaInvestException(KoreaInvestErrorCode.NO_DATA_FOUND);
        }
    }
}
