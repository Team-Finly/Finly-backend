package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeMindDetailResDTO;
import com.umc.finly.domain.home.dto.res.HomeMindResDTO;
import com.umc.finly.domain.home.exception.code.HomeErrorCode;
import com.umc.finly.domain.home.repository.HomeMindRepository;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.member.repository.PersonaRepository;
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
    private final MemberRepository memberRepository;
    private final PersonaRepository personaRepository;


    @Override
    public HomeMindResDTO getHomeMind(Long memberId) {


        // 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HomeErrorCode.HOME_MIND_ACCESS_DENIED));

        // 페르소나 조회
        Persona persona = null;
        if (member.getPersonaId() != null) {
            persona = personaRepository.findById(member.getPersonaId()).orElse(null);
        }


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
        int fmiScore = (int) (
                aScore * 0.4 + bScore * 0.3 + cScore * 0.3
        );

        return HomeMindResDTO.builder()
                .memberName(member.getNickname())
                .persona(
                        HomeMindResDTO.Persona.builder()
                                .personaType(persona.getPersonaType().name())
                                .personaTitle(persona.getTitle())
                                .build()
                )
                .fmiScore(fmiScore)
                .fmiLevel(resolveFmiLevel(fmiScore))
                .fmiComment(resolveFmiComment(fmiScore))
                .scores(
                        HomeMindResDTO.Scores.builder()
                                .resilience(aScore)
                                .decision(bScore)
                                .record(cScore)
                                .build()
                )
                .build();
    }



    @Override
    public HomeMindDetailResDTO getHomeMindDetail(Long memberId) {

        //기존 요약 로직 재사용 (A/B/C, FMI 계산)
        HomeMindResDTO base = getHomeMind(memberId);

        // 사용자 페르소나 재조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HomeErrorCode.HOME_MIND_ACCESS_DENIED));

        Persona persona = null;
        if (member.getPersonaId() != null) {
            persona = personaRepository.findById(member.getPersonaId()).orElse(null);
        }

        int aScore = base.getScores().getResilience();
        int bScore = base.getScores().getDecision();
        int cScore = base.getScores().getRecord();
        int fmi = base.getFmiScore();

        return HomeMindDetailResDTO.builder()
                .memberName(member.getNickname())
                .persona(
                        HomeMindDetailResDTO.Persona.builder()
                                .personaTitle(persona.getTitle())
                                .description(persona.getDescription())
                                .build()
                )
                .fmiScore(fmi)
                .fmiLevel(resolveFmiLevel(fmi))
                .fmiComment(resolveFmiComment(fmi))
                .scores(
                        HomeMindDetailResDTO.Scores.builder()
                                .downMarketResilience(
                                        scoreDetail(aScore, resolveADescription(aScore))
                                )
                                .decisionConsistency(
                                        scoreDetail(bScore, resolveBDescription(bScore))
                                )
                                .recordConsistency(
                                        scoreDetail(cScore, resolveCDescription(cScore))
                                )
                                .build()
                )
                .build();
    }

    //지수 라벨
    private String resolveFmiLevel(int fmi) {
        if (fmi <= 39) return "감정 영향 높음";
        if (fmi <= 69) return "평균적 관리";
        if (fmi <= 84) return "안정적 멘탈";
        return "고도화된 멘탈";
    }

    //지수 설명
    private String resolveFmiComment(int fmi) {
        if (fmi <= 39)
            return "시장의 흐름보다 감정의 영향을 더 많이 받고 있습니다.";
        if (fmi <= 69)
            return "일부 상황에서는 이성적으로 대응하고 있습니다.";
        if (fmi <= 84)
            return "변동성 속에서도 비교적 안정적인 투자 태도를 유지하고 있습니다.";
        return "시장을 감정이 아닌 기준으로 대하고 있습니다.";
    }


    // A/B/C 상세 해석
    private String resolveADescription(int score) { // 하락장 회복 탄력성
        if (score <= 39)
            return "하락장에서 불안과 후회 감정이 자주 기록되고 있습니다.";
        if (score <= 69)
            return "하락장에서는 감정 반응과 이성적 판단이 혼재되어 나타납니다.";
        if (score <= 84)
            return "하락장에서도 비교적 안정적인 감정 반응을 보이고 있습니다.";
        return "하락장에서도 감정 기복이 거의 없이 일관된 태도를 유지합니다.";
    }

    private String resolveBDescription(int score) { // 의사 결정 일치도
        if (score <= 39)
            return "확신 상태에서 내린 판단과 실제 시장 결과의 괴리가 큽니다.";
        if (score <= 69)
            return "일부 판단은 시장 결과와 일치하나 편차가 존재합니다.";
        if (score <= 84)
            return "판단과 실제 시장 흐름이 비교적 잘 일치합니다.";
        return "자신의 판단 기준이 명확하며 시장 결과와 높은 정합성을 보입니다.";
    }

    private String resolveCDescription(int score) { // 기록 성실도
        if (score <= 39)
            return "감정 기록이 불규칙하여 자기 복기가 어려운 상태입니다.";
        if (score <= 69)
            return "기록은 하고 있으나 일관성은 아직 부족합니다.";
        if (score <= 84)
            return "기록 습관이 비교적 잘 형성되어 있습니다.";
        return "기록을 통해 자신의 감정과 판단을 매우 잘 복기하고 있습니다.";
    }

    private HomeMindDetailResDTO.ScoreDetail scoreDetail(int score, String description) {
        return HomeMindDetailResDTO.ScoreDetail.builder()
                .score(score)
                .description(description)
                .build();
    }
}
