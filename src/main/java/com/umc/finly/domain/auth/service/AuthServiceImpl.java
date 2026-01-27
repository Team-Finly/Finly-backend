package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.req.AuthSignUpReq;
import com.umc.finly.domain.auth.dto.res.AuthSignUpRes;
import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.entity.mapping.MemberTerm;
import com.umc.finly.domain.auth.enums.TermType;
import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.auth.repository.TermRepository;
import com.umc.finly.domain.member.dto.request.PersonaAnswerReq;
import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReq;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.member.repository.MemberTermRepository;
import com.umc.finly.domain.member.service.PersonaScoringService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;

    private final PersonaScoringService personaScoringService;
    private final MemberPersonaResultRepository memberPersonaResultRepository;

    // 비밀번호 양식
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
    // 닉네임 양식
    private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,}$";

    // 필수 약관 정의
    private static final EnumSet<TermType> REQUIRED_TERMS =
            EnumSet.of(TermType.TERMS_AGREED, TermType.PRIVACY_AGREED);

    // 이메일 중복 확인
    @Override
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    // 회원가입
    @Override
    public AuthSignUpRes signup(AuthSignUpReq request){
        // 1. 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())){
            throw new CustomException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 비밀번호 정책 검증
        if (!isValidPassword(request.getPassword())){
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }

        // 3. 닉네임 검증
        if (!isValidNickname(request.getNickname())){
            throw new CustomException(AuthErrorCode.INVALID_NICKNAME);
        }

        // 4. 약관 요청 유효성 + 필수 약관 동의 검증
        Map<Long, Boolean> agreedMap = toAgreedMap(request.getTermAgreements());
        validateRequiredTermsAgreed(agreedMap);

        // 5. 페르소나 계산
        Persona persona = resolvePersonaFromSignup(request.getPersonaAnswers());

        // 6. member 생성 + 비번 해시 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .personaId(persona.getId())
                .build();

        Member savedMember = memberRepository.save(member);

        // 7. 페르소나 결과 member_persona_results 에 저장
        MembersPersonasResult personaResult = MembersPersonasResult.create(savedMember.getId(), persona);
        memberPersonaResultRepository.save(personaResult);

        // 8. 약관 동의 저장
        saveTermAgreements(savedMember, agreedMap);

        return AuthSignUpRes.builder()
                .memberId(savedMember.getId())
                .email(savedMember.getEmail())
                .nickname(savedMember.getNickname())
                .personaId(persona.getId())
                .build();
    }

    private boolean isValidPassword(String raw) {
        return raw != null && raw.matches(PASSWORD_REGEX);
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null && nickname.matches(NICKNAME_REGEX);
    }

    private Map<Long, Boolean> toAgreedMap(List<AuthSignUpReq.TermAgreementReq> agreements) {
        if (agreements == null || agreements.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_TERM_REQUEST);
        }

        // termId 중복 요청 방지 겸 정규화
        return agreements.stream().collect(Collectors.toMap(
                AuthSignUpReq.TermAgreementReq::getTermId,
                a -> Boolean.TRUE.equals(a.getAgreed()),
                (a, b) -> a // 중복이면 앞값 유지
        ));
    }

    private void validateRequiredTermsAgreed(Map<Long, Boolean> agreedMap) {
        // 필수 termType들의 id를 구해서, 해당 id가 agreed=true인지 확인
        List<Term> requiredTerms = REQUIRED_TERMS.stream()
                .map(tt -> termRepository.findByTermType(tt)
                        .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_TERM_REQUEST)))
                .toList();

        boolean ok = requiredTerms.stream().allMatch(term ->
                Boolean.TRUE.equals(agreedMap.get(term.getId()))
        );

        if (!ok) {
            throw new CustomException(AuthErrorCode.REQUIRED_TERM_NOT_AGREED);
        }
    }

    private Persona resolvePersonaFromSignup(List<PersonaAnswerReq> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_PERSONA_ANSWERS);
        }

        List<PersonaAnswerReq> convertedAnswers = answers.stream()
                .map(a -> new PersonaAnswerReq(
                        a.getQuestionId(),
                        a.getOptionId()
                ))
                .toList();

        return personaScoringService.resolvePersona(convertedAnswers);
    }

    private void saveTermAgreements(Member member, Map<Long, Boolean> agreedMap) {
        List<Term> allTerms = termRepository.findAll();

        List<Long> termIds = new ArrayList<>(agreedMap.keySet());

        // termId들이 실제 DB에 존재하는지 체크 + 매핑용 Map 구성
        Map<Long, Term> termMap = termRepository.findAllById(termIds).stream()
                .collect(Collectors.toMap(Term::getId, Function.identity()));

        if (termMap.size() != termIds.size()) {
            throw new CustomException(AuthErrorCode.INVALID_TERM_REQUEST);
        }

        List<MemberTerm> rows = termIds.stream()
                .map(termId -> MemberTerm.builder()
                        .member(member)
                        .term(termMap.get(termId))
                        .isAgreed(Boolean.TRUE.equals(agreedMap.get(termId)))
                        .build())
                .toList();

        memberTermRepository.saveAll(rows);
    }
}