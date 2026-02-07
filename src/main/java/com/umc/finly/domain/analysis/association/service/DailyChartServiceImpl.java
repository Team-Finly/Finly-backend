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
        List<Map<String, Object>> limitedRaw = new ArrayList<>();
        int daysOffset = 14; // 기본 14일 전부터 조회 시작
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        while (limitedRaw.size() < 7) {
            String startDateStr = today.minusDays(daysOffset).format(formatter);
            String endDateStr = today.format(formatter);

            List<Map<String, Object>> rawData = dailyChartApiCaller.fetchDailyCandles(symbol, startDateStr, endDateStr);

            if (rawData != null && !rawData.isEmpty()) {
                limitedRaw = rawData.stream().limit(7).collect(Collectors.toList());
            }

            // 7개가 안모였다면 더 과거(30일 전)로 범위를 넓혀서 재시도
            if (limitedRaw.size() < 7) {
                daysOffset += 16;
                if (daysOffset > 100) break;
            }
        }

        // 오늘 날짜와 요일 문자열 생성
        String formattedToday = String.format("%s (%s)",
                today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                today.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.KOREAN));

        // 실제 조회된 기간(확보된 실제 영업일들의 시작일과 종료일) 계산
        LocalDate actualStartDate = LocalDate.parse(limitedRaw.get(limitedRaw.size() -1).get("stck_bsop_date").toString(), formatter);
        LocalDate acutalEndDate = LocalDate.parse(limitedRaw.get(0).get("stck_bsop_date").toString(), formatter);

        // 해당 기간 동안 사용자 기록 조회 (recordDate 기준)
        List<RecordEntry> records = recordEntryRepository.findAllByMemberIdAndStockIdAndRecordDateBetween(memberId, stock.getId(), actualStartDate, acutalEndDate);
        // 그룹화 (recordDate를 키값으로 사용)
        Map<LocalDate, List<RecordEntry>> recordMap = records.stream()
                .collect(Collectors.groupingBy(RecordEntry::getRecordDate));

        // 데이터 결합 및 변환
        List<DailyChartResDTO.DailyDataDto> dailyData = limitedRaw.stream()
                .map(m -> {
                    LocalDate d = LocalDate.parse(m.get("stck_bsop_date").toString(), formatter);

                    // 해당 날짜의 기록들 필터링 (Map에서 해당 날짜 (d)에 맞는 기록 리스트 추출)
                    List<RecordEntry> dayRecords = recordMap.getOrDefault(d, Collections.emptyList());

                    // 기록된 모든 감정 (emotionCode Enum 스트림 처리: 중복 제거 후 String 리스트로 변환)
                    List<String> emotions = dayRecords.stream()
                            .map(r -> r.getEmotionCode().name()) // Enum -> "REGRET"
                            .distinct() // 중복 제거
                            .toList();

                    // 가장 많이 기록된 감정 추출 (mainEmotion)
                    String mainEmotion = dayRecords.stream()
                            .collect(Collectors.groupingBy(r -> r.getEmotionCode().name()))
                            .entrySet().stream()
                            .max((e1, e2) -> {
                                List<RecordEntry> list1 = e1.getValue();
                                List<RecordEntry> list2 = e2.getValue();

                                // 1순위: 기록 횟수 비교 (기록 횟수 최대인 감정 선택)
                                if (list1.size() != list2.size()) {
                                    return Integer.compare(list1.size(), list2.size());
                                }

                                // 2순위: 최대 강도 비교 (최대 강도 가장 강한 감정 선택)
                                int maxInt1 = list1.stream().mapToInt(RecordEntry::getEmotionIntensity).max().orElse(0);
                                int maxInt2 = list2.stream().mapToInt(RecordEntry::getEmotionIntensity).max().orElse(0);
                                if (maxInt1 != maxInt2) {
                                    return Integer.compare(maxInt1, maxInt2);
                                }

                                // 3순위: createdAt 비교 (가장 최근 생성된 감정 선택)
                                LocalDateTime latest1 = list1.stream().map(RecordEntry::getCreatedAt).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN);
                                LocalDateTime latest2 = list2.stream().map(RecordEntry::getCreatedAt).max(LocalDateTime::compareTo).orElse(LocalDateTime.MIN);

                                return latest1.compareTo(latest2);
                            })
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
                .endDate(acutalEndDate.toString())
                .dailyData(dailyData)
                .build();
    }
}
