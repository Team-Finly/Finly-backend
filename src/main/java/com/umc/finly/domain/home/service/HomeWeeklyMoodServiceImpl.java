package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.HomeWeeklyMoodRes;
import com.umc.finly.domain.home.exception.code.HomeErrorCode;
import com.umc.finly.domain.home.repository.HomeRecordRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeWeeklyMoodServiceImpl implements HomeWeeklyMoodService {

    private final HomeRecordRepository homeRecordRepository;

    @Override
    public HomeWeeklyMoodRes getWeeklyMood(Long memberId) {


        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);


        List<RecordEntry> records =
                homeRecordRepository.findByMemberIdAndRecordDateBetween(
                        //  //  ️이번 주 동안 사용자가 작성한 모든 기록 조회 recordDate 기준
                        memberId, monday, sunday
                ).orElseThrow(() ->
                        new CustomException(HomeErrorCode.HOME_WEEKLY_MOOD_INTERNAL_ERROR) // 에러 추가
                );

        //기록을 요일(DayOfWeek) 기준으로 그룹핑
        Map<DayOfWeek, List<RecordEntry>> grouped =
                records.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getRecordDate().getDayOfWeek()
                        ));

        //월요일 ~ 일요일 순서로 위클리 무드 응답 생성
        List<HomeWeeklyMoodRes.DayMood> days = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {

            // 해당 요일의 기록 목록 (없으면 빈 리스트로 반환)
            List<RecordEntry> dayRecords =
                    grouped.getOrDefault(day, List.of());

            //해당 요일에 기록이 없는 경우 null, false로 설정
            if (dayRecords.isEmpty()) {
                days.add(HomeWeeklyMoodRes.DayMood.builder()
                        .dayOfWeek(day.name().substring(0, 3))
                        .hasRecord(false)
                        .emotion(null)
                        .build());
                continue;
            }

            //해당 요일에 여러 개의 기록이 있는 경우 가장 마지막(createdAt 기준) 기록을 선택
            RecordEntry lastRecord =
                    dayRecords.stream()
                            .max(Comparator.comparing(RecordEntry::getCreatedAt))
                            .orElseThrow();

            days.add(HomeWeeklyMoodRes.DayMood.builder()
                    .dayOfWeek(day.name().substring(0, 3))
                    .hasRecord(true)
                    .emotion(lastRecord.getEmotionCode())
                    .build());
        }

        return HomeWeeklyMoodRes.builder()
                .days(days)
                .build();
    }


}
