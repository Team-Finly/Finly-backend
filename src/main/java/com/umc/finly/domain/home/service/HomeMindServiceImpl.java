package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.dto.res.HomeMindRes;
import com.umc.finly.domain.home.exception.code.HomeErrorCode;
import com.umc.finly.domain.home.repository.HomeMindRepository;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.member.repository.PersonaRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.domain.home.exception.code.HomeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeMindServiceImpl implements HomeMindService {

    private final HomeMindRepository homeMindRepository;
    private final MemberRepository memberRepository;
    private final PersonaRepository personaRepository;

    @Override
    public HomeMindRes getHomeMind(Long memberId) {


        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new CustomException(HomeErrorCode.HOME_MIND_MEMBER_NOT_FOUND)
                );

        // Persona 조회
        Long personaId = member.getPersonaId();

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() ->
                        new CustomException(HomeErrorCode.HOME_MIND_PERSONA_NOT_FOUND)
                );


        //FIM 계산
        int mindIndex;
        try {
            mindIndex = calculateMindIndex(memberId);
        } catch (Exception e) {
            throw new CustomException(HomeErrorCode.HOME_MIND_CALCULATION_FAILED);
        }

        // 점수 구간 해석
        String grade;
        String description;

        if (mindIndex < 40) {
            grade = "감정 영향 높음";
            description = "시장의 흐름보다 감정의 영향을 더 많이 받고 있습니다.";
        } else if (mindIndex < 70) {
            grade = "평균적 대응";
            description = "일부 상황에서는 이성적으로 대응하고 있습니다.";
        } else if (mindIndex < 85) {
            grade = "안정적 멘탈";
            description = "변동성 속에서도 비교적 안정적인 투자 태도를 유지하고 있습니다.";
        } else {
            grade = "고도화된 멘탈";
            description = "시장을 감정이 아닌 기준으로 대하고 있습니다.";
        }

        return HomeMindRes.builder()
                .userName(member.getNickname())
                .personaTitle(persona.getTitle())
                .mindIndex(mindIndex)
                .grade(grade)
                .description(description)
                .build();
    }

    /**
     * FMI 계산 로직
     */
    private int calculateMindIndex(Long memberId) {

        YearMonth now = YearMonth.now();
        LocalDate start = now.atDay(1);
        LocalDate end = now.atEndOfMonth();

        List<LocalDate> recordedDates =
                homeMindRepository.findRecordedDatesInPeriod(memberId, start, end);

        int recordDays = recordedDates.size();
        int businessDays = 20; // 실제 영업일

        int cScore = Math.min(100, (recordDays * 100) / businessDays); //기록 성실도

        int aScore = 60; // 하락장 회복 탄력성 (일단은 더미로)
        int bScore = 60; // 의사 결정 일치도  (일단은 더미로)

        return (int) ((aScore * 0.4) + (bScore * 0.3) + (cScore * 0.3));
    }
}
