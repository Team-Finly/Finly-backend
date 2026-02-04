package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.converter.FragmentConverter;
import com.umc.finly.domain.record.dto.response.FragmentSummaryResDTO;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.exception.code.RecordErrorCode;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FragmentServiceImpl implements FragmentService {

    private final RecordEntryRepository recordEntryRepository;
    private final FragmentConverter fragmentConverter;

    @Override
    public FragmentSummaryResDTO getFragmentSummary(Long memberId) {
        // memberId가 없으면 잘못된 요청으로 처리
        if (memberId == null) {
            throw new CustomException(RecordErrorCode.RECORD_INVALID_REQUEST);
        }

        // 전체 기록 개수 조회
        long total = recordEntryRepository.countByMemberId(memberId);

        // 기록이 0개면 정상 케이스로 빈 응답 반환 (0으로 나누기 방지)
        if (total == 0) {
            return fragmentConverter.toFragmentSummaryRes(
                    0L,
                    null,
                    Collections.emptyList()
            );
        }

        // 감정별 집계 조회
        List<RecordEntryRepository.EmotionCountProjection> projections =
                recordEntryRepository.countGroupByEmotionCode(memberId);

        // dominantType(현재까지 1등 감정) 계산
        EmotionCode dominantType = null;
        long maxCount = -1;

        // TypeSummary 리스트 생성
        List<FragmentSummaryResDTO.TypeSummary> summaries = new ArrayList<>();

        for (RecordEntryRepository.EmotionCountProjection p : projections) {
            // count 값 null 방어 + 0이면 스킵
            long count = p.getCount() == null ? 0L : p.getCount();

            // count 0이면 목록에서 제외
            if (count == 0) {
                continue;
            }

            // dominantType 최신화 (가장 많은 감정 찾기)
            if (dominantType == null || count > maxCount) {
                dominantType = p.getEmotionCode();
                maxCount = count;
            }

            // percent 계산
            int percent = (int) Math.round(count * 100.0 / total);

            summaries.add(FragmentSummaryResDTO.TypeSummary.builder()
                    .type(p.getEmotionCode())
                    .count(count)
                    .percent(percent)
                    .build());
        }

        // percent 합이 100이 되도록 보정
        adjustPercentTo100(summaries);

        // 최종 응답은 count 기준 내림차순 정렬
        summaries.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        // DTO 조립은 Converter에 위임
        return fragmentConverter.toFragmentSummaryRes(total, dominantType, summaries);
    }

    // percent 합이 100이 되도록 보정하는 메서드
    private void adjustPercentTo100(List<FragmentSummaryResDTO.TypeSummary> summaries) {
        // percent 총합 계산
        int sum = 0;
        for (FragmentSummaryResDTO.TypeSummary s : summaries) {
            sum += s.getPercent();
        }

        int diff = 100 - sum;

        // 보정할 필요가 없으면 종료
        if (diff == 0 || summaries.isEmpty()) {
            return;
        }

        if (diff > 0) {
            // 퍼센트가 부족한 경우: count 큰 순서대로 +1
            summaries.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

            for (int i = 0; i < diff; i++) {
                FragmentSummaryResDTO.TypeSummary target = summaries.get(i % summaries.size());
                target.setPercent(target.getPercent() + 1);
            }
        } else {
            // 퍼센트가 초과한 경우: count 작은 순서대로 -1
            summaries.sort((a, b) -> Long.compare(a.getCount(), b.getCount()));

            for (int i = 0; i < -diff; i++) {
                FragmentSummaryResDTO.TypeSummary target =  summaries.get(i % summaries.size());
                if (target.getPercent() > 0) {
                    target.setPercent(target.getPercent() - 1);
                }
            }
        }
    }
}
