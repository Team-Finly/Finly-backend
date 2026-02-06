package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeMindResDTO;
import com.umc.finly.domain.home.exception.code.HomeErrorCode;
import com.umc.finly.domain.home.repository.HomeMindRepository;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeMindServiceImpl implements HomeMindService {

    private final HomeMindRepository homeMindRepository;

    @Override
    public HomeMindResDTO getHomeMind(Long memberId) {


        // 사용자 + 페르소나 조회
        Object[] result = homeMindRepository.findMemberWithPersona(memberId)
                .orElseThrow(() ->
                        new CustomException(HomeErrorCode.HOME_MIND_ACCESS_DENIED));

        Member member = (Member) result[0];
        Persona persona = (Persona) result[1];


        // C. 기록 성실도
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);

        int recordedDays = homeMindRepository
                .findDistinctRecordDates(memberId, startOfMonth, now)
                .size();

        int businessDays = now.lengthOfMonth();
        int cScore = (int) ((double) recordedDays / businessDays * 100);


        // B. 의사결정 일치도
        List<RecordEntry> confidenceBuys =
                homeMindRepository.findByMemberIdAndEmotionCodeAndTradeAction(
                        memberId,
                        EmotionCode.CONFIDENCE,
                        TradeAction.BUY
                );

        int totalConfidence = confidenceBuys.size();
        int hitCount = 0;

        for (RecordEntry record : confidenceBuys) {
            // 현재는 가격 히스토리 미연동 → 임시 정책: 기록 존재 시 적중 처리
            hitCount++;
        }

        int bScore = totalConfidence == 0 ? 0 :
                (int) ((double) hitCount / totalConfidence * 100);

        // A. 하락장 회복탄력성
        LocalDate start = now.minusMonths(1);

        List<RecordEntry> allRecords =
                homeMindRepository.findByMemberIdAndRecordDateBetween(
                        memberId, start, now
                );

        long negativeCount = allRecords.stream()
                .filter(r ->
                        r.getEmotionCode() == EmotionCode.ANXIETY ||
                                r.getEmotionCode() == EmotionCode.REGRET)
                .count();

        int total = allRecords.size();
        int aScore = total == 0 ? 0 :
                (int) ((1 - (double) negativeCount / total) * 100);


        // FMI 계산
        int fmi = (int) (
                aScore * 0.4 +
                        bScore * 0.3 +
                        cScore * 0.3
        );

        return HomeMindResDTO.builder()
                .nickname(member.getNickname())
                .persona(
                        HomeMindResDTO.Persona.builder()
                                .personaType(persona.getPersonaType().name())
                                .title(persona.getTitle())
                                .build()
                )
                .fmi(fmi)
                .levelMessage(resolveMessage(fmi))
                .scores(
                        HomeMindResDTO.Scores.builder()
                                .resilience(aScore)
                                .decision(bScore)
                                .record(cScore)
                                .build()
                )
                .build();
    }

    private String resolveMessage(int fmi) {
        if (fmi < 40) return "시장의 흐름보다 감정의 영향을 더 많이 받고 있습니다.";
        if (fmi < 70) return "일부 상황에서는 이성적으로 대응하고 있습니다.";
        if (fmi < 85) return "변동성 속에서도 비교적 안정적인 투자 태도를 유지하고 있습니다.";
        return "시장을 감정이 아닌 기준으로 대하고 있습니다.";
    }
}
