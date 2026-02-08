package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.HourlyChartConverter;
import com.umc.finly.domain.analysis.association.dto.HourlyChartResDTO;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestErrorCode;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestException;
import com.umc.finly.domain.analysis.association.infra.HourlyChartApiCaller;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.domain.record.repository.RecordFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HourlyChartServiceImpl implements HourlyChartService {

    private final StockRepository stockRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final RecordFeedbackRepository recordFeedbackRepository;
    private final HourlyChartApiCaller chartApiCaller;

    @Override
    public HourlyChartResDTO getHourlyChart(Long memberId, String symbol, LocalDate targetDate) {
        // 1. Symbol로 Stock 엔티티 조회 (stockId를 얻기 위함)
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new KoreaInvestException(KoreaInvestErrorCode.STOCK_NOT_FOUND));
        Long stockId = stock.getId();

        // 2. 주가 데이터 API 호출 및 가공
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<Map<String, Object>> rawOutput = chartApiCaller.fetchHourlyChart(symbol, formattedDate);

        List<HourlyChartResDTO.PriceData> prices = new ArrayList<>();

        // 3. 데이터 존재 여부 및 휴장일 체크
        if (!rawOutput.isEmpty()) {
            String actualDate = rawOutput.get(0).get("stck_bsop_date").toString().replaceAll("\"", "");

            // 요청 날짜와 API 응답 날짜가 같을 때만 파싱 진행 (다르면 휴장일이므로 prices는 빈 리스트 유지)
            if (formattedDate.equals(actualDate)) {
                prices = parsePrices(rawOutput, targetDate);
            }
        }
        // rawOutput이 비어있다면 prices는 빈 리스트가 됨

        // 3. 감정 기록 조회 (추출한 stockId 사용)
        List<RecordEntry> records = recordEntryRepository.findAllByMemberIdAndStockIdAndRecordDate(memberId, stockId, targetDate);
        List<Long> recordIds = records.stream().map(RecordEntry::getId).toList();

        // 4. 피드백 조회 및 Map 구성
        Map<Long, RecordFeedback> feedbackMap = recordFeedbackRepository.findAllByRecordEntryIdInAndMemberId(recordIds, memberId)
                .stream()
                .collect(Collectors.toMap(
                        RecordFeedback::getRecordEntryId,
                        fb -> fb,
                        (existing, replacement) -> existing
                ));

        // 5. Converter를 통해 최종 DTO 생성
        return HourlyChartConverter.toHourlyChartResDTO(stock, targetDate, prices, records, feedbackMap);
    }

    private List<HourlyChartResDTO.PriceData> parsePrices(List<Map<String, Object>> rawOutput, LocalDate targetDate) {
        LocalDate today = LocalDate.now();
        LocalTime limitTime = LocalTime.now().minusMinutes(1);

        return rawOutput.stream()
                .map(node -> {
                    String date = node.get("stck_bsop_date").toString().replaceAll("\"", "");
                    String rawHour = node.get("stck_cntg_hour").toString().replaceAll("\"", "");

                    // "yyyy-MM-dd HH:mm" 포맷 생성
                    String formattedDateTime = String.format("%s-%s-%s %s:%s",
                            date.substring(0, 4), date.substring(4, 6), date.substring(6, 8),
                            rawHour.substring(0, 2), rawHour.substring(2, 4));

                    BigDecimal price = new BigDecimal(node.get("stck_prpr").toString().replaceAll("\"", ""));
                    return new HourlyChartResDTO.PriceData(formattedDateTime, price);
                })
                .filter(p -> {
                    LocalTime t = LocalTime.parse(p.dateTime().substring(11));
                    boolean withinRange = !t.isBefore(LocalTime.of(9, 0)) && !t.isAfter(LocalTime.of(15, 30));
                    // 오늘이면 현재 시각 1분 전까지만 표시
                    return targetDate.equals(today) ? withinRange && !t.isAfter(limitTime) : withinRange;
                })
                .sorted(Comparator.comparing(HourlyChartResDTO.PriceData::dateTime))
                .toList();
    }
}