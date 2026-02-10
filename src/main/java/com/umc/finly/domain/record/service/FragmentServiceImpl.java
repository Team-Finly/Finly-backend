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
import java.time.LocalDateTime;
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
                    false,
                    null,
                    false,
                    Collections.emptyList()
            );
        }

        // 감정별 집계 조회
        List<FragmentRepository.EmotionCountProjection> projections =
                fragmentRepository.countGroupByEmotionCode(memberId);

        // 파이차트용 summaries 구성
        List<FragmentSummaryResDTO.TypeSummary> summaries = new ArrayList<>();

        // dominant/recessive 산출용 집계 리스트
        List<EmotionAgg> aggs = new ArrayList<>();

        for (FragmentRepository.EmotionCountProjection p : projections) {
            // null 방어
            long count = p.getCount() == null ? 0L : p.getCount();

            // count가 0이면 목록에서 제외
            if (count == 0) {
                continue;
            }

            // 퍼센트 계산 (반올림)
            int percent = (int) Math.round(count * 100.0 / total);

            // typeSummary용 DTO 구성
            summaries.add(FragmentSummaryResDTO.TypeSummary.builder()
                    .type(p.getEmotionCode())
                    .count(count)
                    .percent(percent)
                    .build());

            // dominant/recessive 판별용 데이터 적재
            aggs.add(new EmotionAgg(
                    p.getEmotionCode(),
                    count,
                    p.getLatestAt()            
            ));
        }

        // 퍼센트 합이 100이 되도록 보정 (반올림 오차 수정)
        adjustPercentTo100(summaries);

        // count 기준 내림차순 정렬 + 동률 시 한글 라벨 ㄱㄴㄷ순
        summaries.sort((a, b) -> {
            int cmp = Long.compare(b.getCount(), a.getCount()); // 내림차순
            if (cmp != 0) return cmp;

            // 동률 시 한글 라벨 ㄱㄴㄷ순 정렬
            String al = (a.getType() == null) ? "" : a.getType().getLabel();
            String bl = (b.getType() == null) ? "" : b.getType().getLabel();
            return al.compareTo(bl);
        });

        // 집계 데이터가 없으면(이론상 total>0이면 없기 어렵지만) 방어
        if (aggs.isEmpty()) {
            return fragmentConverter.toFragmentSummaryRes(
                    total,
                    null,
                    false,
                    null,
                    false,
                    summaries
            );
        }

        // dominant 비교 기준: count desc, 동률이면 latestAt desc
        java.util.Comparator<EmotionAgg> dominantComparator = (a, b) -> {
            int cmp = Long.compare(a.count, b.count); // 기본은 count 오름차순
            if (cmp != 0) return cmp;
            // 동률이면 최신시각 오름차순(뒤가 최신) -> max로 뽑을 거라 최신이 선택됨
            return compareLatestAsc(a.latestAt, b.latestAt);
        };

        // recessive 비교 기준: count asc, 동률이면 latestAt desc
        // min으로 뽑을 거라 "동률이면 최신이 선택"되게 latestAt을 반대로(내림차순) 넣어줌
        java.util.Comparator<EmotionAgg> recessiveComparator = (a, b) -> {
            int cmp = Long.compare(a.count, b.count); // count 오름차순
            if (cmp != 0) return cmp;
            // 동률이면 최신이 앞으로 오도록 (내림차순)
            return compareLatestDesc(a.latestAt, b.latestAt);
        };

        // dominantType 산출 (max)
        EmotionAgg dominantAgg = Collections.max(aggs, dominantComparator);
        EmotionCode dominantType = dominantAgg.type;

        // recessiveType 산출 (min)
        EmotionAgg recessiveAgg = Collections.min(aggs, recessiveComparator);
        EmotionCode recessiveType = recessiveAgg.type;

        // 동률 여부 계산 (max/min count 기준)
        long maxCount = aggs.stream().mapToLong(a -> a.count).max().orElse(0L);
        long minCount = aggs.stream().mapToLong(a -> a.count).min().orElse(0L);

        boolean isMultipleDominant = aggs.stream().filter(a -> a.count == maxCount).count() >= 2;
        boolean isMultipleRecessive = aggs.stream().filter(a -> a.count == minCount).count() >= 2;

        // DTO 응답 변환
        return fragmentConverter.toFragmentSummaryRes(
                total,
                dominantType,
                isMultipleDominant,
                recessiveType,
                isMultipleRecessive,
                summaries
        );
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
                    // 안정적인 정렬: count 내림차순, 같으면 감정 한글 라벨 기준 ㄱㄴㄷ 정렬
                    .sorted((a, b) -> {
                        int cmp = Long.compare(b.getCount(), a.getCount());
                        if (cmp != 0) return cmp;
                        String at = (a.getType() == null) ? "" : a.getType().getLabel();
                        String bt = (b.getType() == null) ? "" : b.getType().getLabel();
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

    // dominant/recessive 산출을 위한 내부 집계 모델
    private static class EmotionAgg {
        private final EmotionCode type;
        private final long count;
        private final LocalDateTime latestAt;

        private EmotionAgg(EmotionCode type, long count, LocalDateTime latestAt) {
            this.type = type;
            this.count = count;
            this.latestAt = latestAt;
        }
    }

    // latestAt 오름차순 비교(null 안전) - max에서 최신이 선택되도록 사용
    private int compareLatestAsc(LocalDateTime a, LocalDateTime b) {
        // 둘 다 null이면 동일
        if (a == null && b == null) return 0;
        // null은 뒤로
        if (a == null) return 1;
        if (b == null) return -1;
        // 오래된 게 앞으로
        return a.compareTo(b);
    }

    // latestAt 내림차순 비교(null 안전)
    private int compareLatestDesc(LocalDateTime a, LocalDateTime b) {
        // 둘 다 null이면 동일
        if (a == null && b == null) return 0;
        // null은 뒤로
        if (a == null) return 1;
        if (b == null) return -1;
        // 최신이 앞으로
        return b.compareTo(a);
    }
}
