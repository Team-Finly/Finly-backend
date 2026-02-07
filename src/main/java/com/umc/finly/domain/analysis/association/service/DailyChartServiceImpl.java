package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.converter.DailyChartConverter;
import com.umc.finly.domain.analysis.association.dto.DailyChartResDTO;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestErrorCode;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestException;
import com.umc.finly.domain.analysis.association.infra.DailyChartApiCaller;
import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyChartServiceImpl implements DailyChartService {

    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;
    private final DailyChartApiCaller dailyChartApiCaller;

    @Override
    public DailyChartResDTO getDailyChart(Long memberId, String symbol) {

        // symbol 검증
        if (symbol == null || !symbol.matches("^\\d{6}$")) {
            throw new KoreaInvestException(KoreaInvestErrorCode.INVALID_SYMBOL);
        }

        // symbol로 종목 정보(id, name) 조회
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new KoreaInvestException(KoreaInvestErrorCode.STOCK_NOT_FOUND));

        // 주가 데이터 가져오기 (영업일 7개가 확보될 때까지 범위를 넓혀가며 조회)
        List<Map<String, Object>> rawData = new ArrayList<>();
        int daysOffset = 14; // 기본 14일 전부터 조회 시작

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        while (daysOffset <= 100) {
            String startDateStr = today.minusDays(daysOffset).format(formatter);
            String endDateStr = today.format(formatter);

            // API 호출
            List<Map<String, Object>> currentRaw = dailyChartApiCaller.fetchDailyCandles(symbol, startDateStr, endDateStr);

            if (currentRaw != null && !currentRaw.isEmpty()) {
                rawData = currentRaw; // 최신 결과로 교체
                if (rawData.size() >= 7) break; // 7개 확보 시 즉시 탈출
            }

            // 데이터가 없거나 7개 미만이면 더 과거로
            daysOffset += 16;
        }

        // 최종 결과가 7개보다 많을 수 있으므로 마지막에 limit(7) 처리
        List<Map<String, Object>> limitedRaw = rawData.stream()
                .limit(7)
                .collect(Collectors.toList());

        if (limitedRaw.isEmpty()) {
            throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
        }

        // 오늘 날짜와 요일 문자열 생성
        String formattedToday = String.format("%s (%s)",
                today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                today.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.KOREAN));

        // 실제 조회된 기간(확보된 실제 영업일들의 시작일과 종료일) 계산
        String rawStart = String.valueOf(limitedRaw.get(limitedRaw.size() - 1).get("stck_bsop_date"));
        String rawEnd = String.valueOf(limitedRaw.get(0).get("stck_bsop_date"));
        LocalDate actualStartDate = LocalDate.parse(rawStart, formatter);
        LocalDate actualEndDate = LocalDate.parse(rawEnd, formatter);

        // 해당 기간 동안 사용자 기록 조회 (recordDate 기준)
        List<RecordEntry> records = recordEntryRepository.findAllByMemberIdAndStockIdAndRecordDateBetween(memberId, stock.getId(), actualStartDate, actualEndDate);
        // 그룹화 (recordDate를 키값으로 사용)
        Map<LocalDate, List<RecordEntry>> recordMap = records.stream()
                .collect(Collectors.groupingBy(RecordEntry::getRecordDate));

        // 데이터 결합 및 변환
        List<DailyChartResDTO.DailyDataDto> dailyData = limitedRaw.stream()
                .map(m -> {
                    // 날짜 파싱
                    LocalDate d = LocalDate.parse(String.valueOf(m.get("stck_bsop_date")), formatter);

                    // 해당 날짜의 기록들 필터링 (Map에서 해당 날짜 (d)에 맞는 기록 리스트 추출)
                    List<RecordEntry> dayRecords = recordMap.getOrDefault(d, Collections.emptyList());

                    // 감정 목록 추출
                    List<String> emotions = dayRecords.stream()
                            .map(r -> r.getEmotionCode().name())
                            .distinct() // 중복 제거
                            .toList();

                    // 가장 많이 기록된 감정 추출 (mainEmotion)
                    String mainEmotion = dayRecords.stream()
                            .collect(Collectors.groupingBy(r -> r.getEmotionCode().name()))
                            .entrySet().stream()
                            .max(Comparator.<Map.Entry<String, List<RecordEntry>>, Integer>comparing(e -> e.getValue().size()) // 1순위: 횟수
                                    .thenComparing(e -> e.getValue().stream() // 2순위: 최대 강도
                                            .mapToInt(RecordEntry::getEmotionIntensity).max().orElse(0))
                                    .thenComparing(e -> e.getValue().stream() // 3순위: 최신 생성순
                                            .map(RecordEntry::getCreatedAt).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN))
                            )
                            .map(Map.Entry::getKey)
                            .orElse(null);

                    return DailyChartConverter.toDailyDataDto(m, dayRecords.size(), emotions, mainEmotion);
                })
                .sorted(Comparator.comparing(DailyChartResDTO.DailyDataDto::getDate))
                .toList();

        return DailyChartResDTO.builder()
                .stockId(stock.getId())
                .symbol(symbol)
                .stockName(stock.getName())
                .today(formattedToday)
                .startDate(actualStartDate.toString())
                .endDate(actualEndDate.toString())
                .dailyData(dailyData)
                .build();
    }
}