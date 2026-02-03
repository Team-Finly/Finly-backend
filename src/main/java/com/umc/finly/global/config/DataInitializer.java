package com.umc.finly.global.config;

import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.enums.TermType;
import com.umc.finly.domain.auth.repository.TermRepository;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.PersonaTestOption;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import com.umc.finly.domain.member.enums.ChoiceCode;
import com.umc.finly.domain.member.enums.PersonaType;
import com.umc.finly.domain.member.enums.QuestionCode;
import com.umc.finly.domain.member.repository.PersonaRepository;
import com.umc.finly.domain.member.repository.PersonaTestOptionsRepository;
import com.umc.finly.domain.member.repository.PersonaTestQuestionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final TermRepository termRepository;
    private final PersonaTestQuestionsRepository questionRepository;
    private final PersonaTestOptionsRepository optionRepository;
    private final PersonaRepository personaRepository;

    @Override
    public void run(ApplicationArguments args) {
        initTerms();
        initPersonaTestQuestions();
        initPersonas();
    }

    private void initTerms() {
        if (termRepository.count() > 0) {
            log.info("Terms 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        Term termsAgreed = Term.builder()
                .title("(필수) 이용약관 동의")
                .termType(TermType.TERMS_AGREED)
                .build();

        Term privacyAgreed = Term.builder()
                .title("(필수) 개인정보처리방침 동의")
                .termType(TermType.PRIVACY_AGREED)
                .build();

        Term marketingAgreed = Term.builder()
                .title("(선택) 마케팅 정보 수신 동의")
                .termType(TermType.MARKETING_AGREED)
                .build();

        termRepository.save(termsAgreed);
        termRepository.save(privacyAgreed);
        termRepository.save(marketingAgreed);

        log.info("Terms 초기 데이터 3건이 생성되었습니다.");
    }

    private void initPersonaTestQuestions() {
        if (questionRepository.count() > 0) {
            log.info("PersonaTestQuestions 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        // Q1: 투자 경험 질문
        PersonaTestQuestion q1 = questionRepository.save(PersonaTestQuestion.builder()
                .questionCode(QuestionCode.Q1)
                .content("주식 투자 경험이 있으신가요?")
                .build());

        // Q2: 투자 목적 질문
        PersonaTestQuestion q2 = questionRepository.save(PersonaTestQuestion.builder()
                .questionCode(QuestionCode.Q2)
                .content("투자의 주된 목적은 무엇인가요?")
                .build());

        // Q3: 리스크 성향 질문
        PersonaTestQuestion q3 = questionRepository.save(PersonaTestQuestion.builder()
                .questionCode(QuestionCode.Q3)
                .content("투자 시 리스크를 어느 정도 감수할 수 있나요?")
                .build());

        // Q1 선택지 (id: 1, 2, 3)
        optionRepository.save(PersonaTestOption.builder()
                .question(q1)
                .choiceCode(ChoiceCode.A)
                .content("네, 경험이 있습니다")
                .build());
        optionRepository.save(PersonaTestOption.builder()
                .question(q1)
                .choiceCode(ChoiceCode.B)
                .content("아니요, 처음입니다")
                .build());
        optionRepository.save(PersonaTestOption.builder()
                .question(q1)
                .choiceCode(ChoiceCode.C)
                .content("조금 해봤습니다")
                .build());

        // Q2 선택지 (id: 4, 5, 6)
        optionRepository.save(PersonaTestOption.builder()
                .question(q2)
                .choiceCode(ChoiceCode.A)
                .content("안정적인 자산 증식")
                .build());
        optionRepository.save(PersonaTestOption.builder()
                .question(q2)
                .choiceCode(ChoiceCode.B)
                .content("단기 수익 실현")
                .build());
        optionRepository.save(PersonaTestOption.builder()
                .question(q2)
                .choiceCode(ChoiceCode.C)
                .content("장기적인 목돈 마련")
                .build());

        // Q3 선택지 (id: 7, 8, 9)
        optionRepository.save(PersonaTestOption.builder()
                .question(q3)
                .choiceCode(ChoiceCode.A)
                .content("리스크를 최소화하고 싶습니다")
                .build());
        optionRepository.save(PersonaTestOption.builder()
                .question(q3)
                .choiceCode(ChoiceCode.B)
                .content("적당한 리스크는 감수합니다")
                .build());
        optionRepository.save(PersonaTestOption.builder()
                .question(q3)
                .choiceCode(ChoiceCode.C)
                .content("높은 수익을 위해 리스크를 감수합니다")
                .build());

        log.info("PersonaTestQuestions 3건, PersonaTestOptions 9건이 생성되었습니다.");
    }

    private void initPersonas() {
        if (personaRepository.count() > 0) {
            log.info("Personas 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        personaRepository.save(Persona.builder()
                .personaType(PersonaType.WORRIED_DEER)
                .title("걱정 많은 사슴")
                .description("투자에 대한 걱정이 많지만, 차근차근 배워나가고 싶은 타입입니다.")
                .build());

        personaRepository.save(Persona.builder()
                .personaType(PersonaType.CAUTIOUS_TURTLE)
                .title("신중한 거북이")
                .description("안정적인 투자를 선호하며, 리스크를 최소화하는 타입입니다.")
                .build());

        personaRepository.save(Persona.builder()
                .personaType(PersonaType.SHARP_EAGLE)
                .title("날카로운 독수리")
                .description("높은 수익을 위해 과감하게 투자하는 공격적인 타입입니다.")
                .build());

        personaRepository.save(Persona.builder()
                .personaType(PersonaType.FIERY_LION)
                .title("불타는 사자")
                .description("적극적이고 열정적으로 투자에 임하는 타입입니다.")
                .build());

        log.info("Personas 초기 데이터 4건이 생성되었습니다.");
    }
}
