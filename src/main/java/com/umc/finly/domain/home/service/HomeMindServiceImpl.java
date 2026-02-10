package com.umc.finly.domain.home.service;

import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.home.converter.HomeMindConverter;
import com.umc.finly.domain.home.dto.res.HomeMindDetailResDTO;
import com.umc.finly.domain.home.dto.res.HomeMindResDTO;
import com.umc.finly.domain.home.exception.HomeErrorCode;
import com.umc.finly.domain.home.repository.HomeMindRepository;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.member.repository.PersonaRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeMindServiceImpl implements HomeMindService {

    private final HomeMindRepository homeMindRepository;
    private final MemberRepository memberRepository;
    private final PersonaRepository personaRepository;

    //내부 계산 결과용 클래스
    private static class MindScoreResult {
        int a;
        int b;
        int c;
        int fmi;
    }


      // 공통 계산 로직
      private MindScoreResult calculateMindScores(Long memberId) {

          LocalDate now = LocalDate.now();

          /* ========= C. 기록 성실도 ========= */
          LocalDate startOfMonth = now.withDayOfMonth(1);
          int recordedDays =
                  homeMindRepository
                          .findDistinctRecordDates(memberId, startOfMonth, now)
                          .size();

          int cScore = (int) ((double) recordedDays / now.lengthOfMonth() * 100);

          /* ========= A. 하락장 회복탄력성 ========= */
          List<FearIndexResult> fearResults =
                  homeMindRepository.findLatestFearIndexResults(memberId);

          int aScore = fearResults.isEmpty()
                  ? 0
                  : Math.max(0, 100 - fearResults.get(0).getFearIndex().intValue());

          /* ========= B. 매수 확신도 ========= */
          List<ConvictionScoreResult> convictionResults =
                  homeMindRepository.findLatestConvictionScoreResults(memberId);

          int bScore = convictionResults.isEmpty()
                  ? 0
                  : convictionResults.get(0).getConvictionScore().intValue();

          int fmi = (int) (
                  aScore * 0.4 +
                          bScore * 0.3 +
                          cScore * 0.3
          );

          MindScoreResult result = new MindScoreResult();
          result.a = aScore;
          result.b = bScore;
          result.c = cScore;
          result.fmi = fmi;
          return result;
      }

      //Home 진입 시 → FMI 계산 → Member에 저장
    @Transactional
    @Override
    public HomeMindResDTO getHomeMind(Long memberId) {//금융 마음 지수 조회 서비스 로직

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HomeErrorCode.HOME_MIND_ACCESS_DENIED));

        Persona persona = member.getPersonaId() == null ? null :
                personaRepository.findById(member.getPersonaId()).orElse(null);

        MindScoreResult score = calculateMindScores(memberId);

        member.updateFinMindIdx(score.fmi);//

        return HomeMindConverter.toHomeMindRes(
                member,
                persona,
                score.fmi,
                resolveFmiLevel(score.fmi),
                resolveFmiComment(score.fmi)
        );
    }


    @Override
    public HomeMindDetailResDTO getHomeMindDetail(Long memberId) {// 금융 마음 지수 상세 조회 서비스 로직

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HomeErrorCode.HOME_MIND_ACCESS_DENIED));

        Persona persona = member.getPersonaId() == null ? null :
                personaRepository.findById(member.getPersonaId()).orElse(null);

        MindScoreResult score = calculateMindScores(memberId);

        HomeMindDetailResDTO.Scores scores =
                HomeMindDetailResDTO.Scores.builder()
                        .downMarketResilience(
                                HomeMindConverter.toScoreDetail(
                                        score.a, resolveADescription(score.a)
                                )
                        )
                        .decisionConsistency(
                                HomeMindConverter.toScoreDetail(
                                        score.b, resolveBDescription(score.b)
                                )
                        )
                        .recordConsistency(
                                HomeMindConverter.toScoreDetail(
                                        score.c, resolveCDescription(score.c)
                                )
                        )
                        .build();

        return HomeMindConverter.toHomeMindDetailRes(
                member,
                persona,
                score.fmi,
                resolveFmiLevel(score.fmi),
                resolveFmiComment(score.fmi),
                scores
        );
    }

    // FMI 라벨 / 설명
    private String resolveFmiLevel(int fmi) {
        if (fmi <= 39) return "감정 영향 높음";
        if (fmi <= 69) return "평균적 관리";
        if (fmi <= 84) return "안정적 멘탈";
        return "고도화된 멘탈";
    }

    private String resolveFmiComment(int fmi) {
        if (fmi <= 39)
            return "시장의 흐름보다 감정의 영향을 더 많이 받고 있습니다.";
        if (fmi <= 69)
            return "일부 상황에서는 이성적으로 대응하고 있습니다.";
        if (fmi <= 84)
            return "변동성 속에서도 비교적 안정적인 투자 태도를 유지하고 있습니다.";
        return "시장을 감정이 아닌 기준으로 대하고 있습니다.";
    }

    //상세 설명
    private String resolveADescription(int score) {
        if (score <= 39)
            return "하락장에서 불안과 후회 감정이 자주 기록되고 있습니다.";
        if (score <= 69)
            return "하락장에서는 감정 반응과 이성적 판단이 혼재되어 나타납니다.";
        if (score <= 84)
            return "하락장에서도 비교적 안정적인 감정 반응을 보이고 있습니다.";
        return "하락장에서도 감정 기복이 거의 없이 일관된 태도를 유지합니다.";
    }

    private String resolveBDescription(int score) {
        if (score <= 39)
            return "확신 상태에서 내린 판단과 실제 시장 결과의 괴리가 큽니다.";
        if (score <= 69)
            return "일부 판단은 시장 결과와 일치하나 편차가 존재합니다.";
        if (score <= 84)
            return "판단과 실제 시장 흐름이 비교적 잘 일치합니다.";
        return "자신의 판단 기준이 명확하며 시장 결과와 높은 정합성을 보입니다.";
    }

    private String resolveCDescription(int score) {
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
