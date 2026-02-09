package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.converter.FragmentConverter;
import com.umc.finly.domain.record.dto.res.FragmentCalendarResDTO;
import com.umc.finly.domain.record.dto.res.FragmentListResDTO;
import com.umc.finly.domain.record.dto.res.FragmentSummaryResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.FragmentPeriodKey;
import com.umc.finly.domain.record.exception.code.RecordErrorCode;
import com.umc.finly.domain.record.repository.FragmentRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 조각 모음함(Fragment) 비즈니스 로직 구현체
// 감정별 기록 통계 및 리스트 조회를 처리함
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FragmentServiceImpl implements FragmentService {

    private final FragmentRepository fragmentRepository;
    private final FragmentConverter fragmentConverter; // DTO 변환 담당

    @Override
    public FragmentSummaryResDTO getFragmentSummary(Long memberId) {
        if (memberId == null) {
            throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
        }

        // 전체 기록 개수 조회
        long total = fragmentRepository.countByMemberId(memberId);

        // 기록이 0개면 빈 응답 반환 (0으로 나누기 방지)
        if (total == 0) {
            return fragmentConverter.toFragmentSummaryRes(
                    0L,
                    null,
                    Collections.emptyList()
            );
        }

        // 감정별 집계 조회
        List<FragmentRepository.EmotionCountProjection> projections =
                fragmentRepository.countGroupByEmotionCode(memberId);

        // 가장 많은 감정(dominantType) 계산
        EmotionCode dominantType = null;
        long maxCount = -1;

        List<FragmentSummaryResDTO.TypeSummary> summaries = new ArrayList<>();

        for (FragmentRepository.EmotionCountProjection p : projections) {
            long count = p.getCount() == null ? 0L : p.getCount();

            // count가 0이면 목록에서 제외
            if (count == 0) {
                continue;
            }

            // 가장 많은 감정 갱신
            if (dominantType == null || count > maxCount) {
                dominantType = p.getEmotionCode();
                maxCount = count;
            }

            // 퍼센트 계산 (반올림)
            int percent = (int) Math.round(count * 100.0 / total);

            summaries.add(FragmentSummaryResDTO.TypeSummary.builder()
                    .type(p.getEmotionCode())
                    .count(count)
                    .percent(percent)
                    .build());
        }

        // 퍼센트 합이 100이 되도록 보정 (반올림 오차 수정)
        adjustPercentTo100(summaries);

        // count 기준 내림차순 정렬
        summaries.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        return fragmentConverter.toFragmentSummaryRes(total, dominantType, summaries);
    }

    @Override
    public FragmentListResDTO getFragmentList(Long memberId, EmotionCode boxType, FragmentPeriodKey periodKey) {
        if (memberId == null) {
            throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
        }

        // periodKey null이면 ALL로 처리
        FragmentPeriodKey key = (periodKey == null) ? FragmentPeriodKey.ALL : periodKey;

        // 기간 조건 계산 (ALL이면 null로 보내 where 조건 무시)
        LocalDate today = LocalDate.now();
        LocalDate from = null;
        LocalDate to = null;

        if (key != FragmentPeriodKey.ALL) {
            from = today.minusMonths(key.getMonths());
            to = today;
        }

        // 조각 리스트 조회 (Stock 조인 포함)
        List<FragmentRepository.FragmentListRowView> rows =
                fragmentRepository.findFragmentList(memberId, boxType, from, to);

        // 총 개수 조회
        long totalCount =
                fragmentRepository.countFragmentList(memberId, boxType, from, to);

        // DTO 조립은 Converter에 위임
        return fragmentConverter.toFragmentListRes(
                boxType, key, from, to, totalCount, rows
        );
    }

    @Override
    public FragmentCalendarResDTO getFragmentCalendar(Long memberId, String yearMonth) {
        // memberId가 없으면 요청 자체가 유효하지 않음
        if (memberId == null) {
            throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
        }

        // yearMonth가 없으면 서버 기준 현재 월로 처리
        final YearMonth ym;
        try {
            // 프론트에서 "2026-02" 형태로 넘겨주는 값 파싱
            ym = (yearMonth == null || yearMonth.isBlank())
                    ? YearMonth.now()
                    : YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            // yyyy-MM 형식이 아니면 400 처리
            throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
        }

        // 조회 범위 계산 (해당 월의 1일 ~ 말일)
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        // 날짜별/감정별 집계 조회
        List<FragmentRepository.CalendarCountProjection> projections =
                fragmentRepository.countCalendarByDateAndEmotion(memberId, from, to);

        // date -> (type -> count) 집계 맵 구성
        java.util.Map<LocalDate, java.util.Map<EmotionCode, Long>> byDate = new java.util.LinkedHashMap<>();
        for (FragmentRepository.CalendarCountProjection p : projections) {
            LocalDate date = p.getRecordDate();
            EmotionCode type = p.getEmotionCode();
            long count = (p.getCount() == null) ? 0L : p.getCount();

            byDate.computeIfAbsent(date, d -> new java.util.EnumMap<>(EmotionCode.class));
            byDate.get(date).put(type, count);
        }

        // 응답 days 리스트 구성 (기록이 있는 날짜만 포함)
        List<FragmentCalendarResDTO.Day> days = new ArrayList<>();
        for (java.util.Map.Entry<LocalDate, java.util.Map<EmotionCode, Long>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            java.util.Map<EmotionCode, Long> typeMap = entry.getValue();

            // totalCount 계산
            long totalCount = 0L;
            for (Long c : typeMap.values()) {
                totalCount += (c == null) ? 0L : c;
            }

            // byType 리스트 구성
            List<FragmentCalendarResDTO.TypeCount> byType = typeMap.entrySet().stream()
                    .map(e -> FragmentCalendarResDTO.TypeCount.builder()
                            .type(e.getKey())
                            .count(e.getValue() == null ? 0L : e.getValue())
                            .build())
                    // 안정적인 정렬: count 내림차순, 같으면 type 이름 오름차순
                    .sorted((a, b) -> {
                        int cmp = Long.compare(b.getCount(), a.getCount());
                        if (cmp != 0) return cmp;
                        String at = (a.getType() == null) ? "" : a.getType().name();
                        String bt = (b.getType() == null) ? "" : b.getType().name();
                        return at.compareTo(bt);
                    })
                    .toList();

            days.add(FragmentCalendarResDTO.Day.builder()
                    .date(date)
                    .totalCount(totalCount)
                    .byType(byType)
                    .build());
        }

        // 총 개수 조회
        long totalCount = fragmentRepository.countByMemberIdAndRecordDateBetween(memberId, from, to);

        // 최종 응답 조립은 Converter에 위임
        return fragmentConverter.toCalendarFragment(ym.toString(), from, to, totalCount, days);
    }

    // 퍼센트 합이 100이 되도록 보정하는 메서드
    // 반올림 오차로 인해 99% 또는 101%가 될 수 있어서 보정 필요
    private void adjustPercentTo100(List<FragmentSummaryResDTO.TypeSummary> summaries) {
        int sum = 0;
        for (FragmentSummaryResDTO.TypeSummary s : summaries) {
            sum += s.getPercent();
        }

        int diff = 100 - sum;

        if (diff == 0 || summaries.isEmpty()) {
            return;
        }

        if (diff > 0) {
            // 퍼센트 부족: count 큰 순서대로 +1
            summaries.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

            for (int i = 0; i < diff; i++) {
                FragmentSummaryResDTO.TypeSummary target = summaries.get(i % summaries.size());
                target.setPercent(target.getPercent() + 1);
            }
        } else {
            // 퍼센트 초과: count 작은 순서대로 -1
            summaries.sort((a, b) -> Long.compare(a.getCount(), b.getCount()));

            int remaining = -diff;
            int attempts = 0;
            int maxAttempts = remaining * summaries.size(); // 무한루프 방지

            while (remaining > 0 && attempts < maxAttempts) {
                FragmentSummaryResDTO.TypeSummary target = summaries.get(attempts % summaries.size());
                if (target.getPercent() > 0) {
                    target.setPercent(target.getPercent() - 1);
                    remaining--;
                }
                attempts++;
            }
        }
    }
}
