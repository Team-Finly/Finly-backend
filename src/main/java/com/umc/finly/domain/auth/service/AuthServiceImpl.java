package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.converter.AuthConverter;
import com.umc.finly.domain.auth.dto.request.AuthLoginReqDTO;
import com.umc.finly.domain.auth.dto.request.AuthSignUpReqDTO;
import com.umc.finly.domain.auth.dto.response.AuthLoginResDTO;
import com.umc.finly.domain.auth.dto.response.AuthSignUpResDTO;
import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.entity.mapping.MemberTerm;
import com.umc.finly.domain.auth.enums.TermType;
import com.umc.finly.domain.auth.exception.code.AuthErrorCode;
import com.umc.finly.domain.auth.repository.TermRepository;
import com.umc.finly.domain.member.dto.request.PersonaAnswerReqDTO;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.mapping.MemberPersonaResults;
import com.umc.finly.domain.member.repository.MemberPersonaResultsRepository;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.member.repository.MemberTermRepository;
import com.umc.finly.domain.member.service.PersonaScoringService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.security.jwt.JwtProvider;
import com.umc.finly.global.util.CookieUtil;
import com.umc.finly.global.security.PasswordPolicy;
import com.umc.finly.global.security.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;
    private final PersonaScoringService personaScoringService;
    private final MemberPersonaResultsRepository memberPersonaResultsRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final CookieUtil cookieUtil;

    // 닉네임 양식
    private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,}$";

    // 필수 약관 정의
    private static final EnumSet<TermType> REQUIRED_TERMS =
            EnumSet.of(TermType.TERMS_AGREED, TermType.PRIVACY_AGREED);

    /** 이메일 중복 확인 **/
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    /** 회원가입 **/
    @Override
    public AuthSignUpResDTO signup(AuthSignUpReqDTO request) {

        // 1. 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 비밀번호 정책 검증
        if (!isValidPassword(request.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }

        // 3. 닉네임 검증
        if (!isValidNickname(request.getNickname())) {
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
        MemberPersonaResults personaResult = MemberPersonaResults.create(savedMember.getId(), persona);
        memberPersonaResultsRepository.save(personaResult);

        // 8. 약관 동의 저장
        saveTermAgreements(savedMember, agreedMap);

        return AuthConverter.toSignUpResDTO(savedMember, persona.getId());
    }

    /** 로그인 **/
    @Override
    public LoginTokens login(AuthLoginReqDTO request) {

        // 1) 멤버 매칭
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_LOGIN_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_LOGIN_PASSWORD);
        }

        // 2) 토큰 발급
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());

        LocalDateTime refreshExpiredAt = LocalDateTime.ofInstant(
                jwtProvider.getExpiration(refreshToken).toInstant(),
                ZoneId.systemDefault()
        );

        member.updateRefreshToken(refreshToken, refreshExpiredAt);
        memberRepository.save(member);

        // 3) max-age 계산
        long refreshMaxAgeSeconds = Math.max(
                0,
                (jwtProvider.getExpiration(refreshToken).getTime() - System.currentTimeMillis()) / 1000
        );

        AuthLoginResDTO body = AuthConverter.toLoginResDTO(accessToken, member);

        return new LoginTokens(body, refreshToken, refreshMaxAgeSeconds);
    }

    /** 토큰 재발급 **/
    @Override
    public ReissueTokens reissue(String refreshToken) {

        // 0) refreshToken 존재 체크 + 정규화
        if (refreshToken == null) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }

        String rawRefreshToken = refreshToken.trim();
        while (rawRefreshToken.startsWith("Bearer ")) {
            rawRefreshToken = rawRefreshToken.substring(7).trim();
        }
        if (rawRefreshToken.isBlank()) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 1) refresh 토큰 1차 검증 (서명/만료/type=refresh)
        try {
            jwtProvider.assertRefreshToken(rawRefreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2) memberId 추출
        final Long memberId;
        try {
            memberId = jwtProvider.getMemberId(rawRefreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3) Member 조회 + 최신 email 사용
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        String email = member.getEmail();

        // 4) DB 만료 검증 (서버 저장 만료 시각)
        LocalDateTime expiredAt = member.getRefreshTokenExpiredAt();
        if (expiredAt == null || !expiredAt.isAfter(LocalDateTime.now())) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 5) 새 토큰 발급
        String newAccessToken = jwtProvider.createAccessToken(memberId, email);

        String newRefreshToken = jwtProvider.createRefreshToken(memberId, email);
        LocalDateTime newRefreshExpiredAt = LocalDateTime.ofInstant(
                jwtProvider.getExpiration(newRefreshToken).toInstant(),
                ZoneId.systemDefault()
        );

        // 6) CAS로 refreshToken 회전
        int updated = memberRepository.rotateRefreshToken(
                memberId,
                rawRefreshToken,
                newRefreshToken,
                newRefreshExpiredAt
        );

        if (updated == 0) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        long refreshMaxAgeSeconds = Math.max(
                0,
                (jwtProvider.getExpiration(newRefreshToken).getTime() - System.currentTimeMillis()) / 1000
        );

        return new ReissueTokens(newAccessToken, newRefreshToken, refreshMaxAgeSeconds);
    }

    /** 로그아웃 **/
    @Override
    public void logout(HttpServletResponse response) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_ACCESS_TOKEN));

        member.clearRefreshToken();
        memberRepository.save(member);
        cookieUtil.clearRefreshTokenCookie(response);
    }

    // -----------------------------------------------------------

    /** 유효성 검사 **/
    private boolean isValidPassword(String raw) {
        return PasswordPolicy.isValid(raw);
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null && nickname.matches(NICKNAME_REGEX);
    }

    // 약관 동의 요청 리스트 맵으로 변환
    private Map<Long, Boolean> toAgreedMap(List<AuthSignUpReqDTO.TermAgreementReq> agreements) {
        if (agreements == null || agreements.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_TERM_REQUEST);
        }

        return agreements.stream().collect(Collectors.toMap(
                AuthSignUpReqDTO.TermAgreementReq::getTermId,
                a -> Boolean.TRUE.equals(a.getAgreed()),
                (a, b) -> a
        ));
    }

    // 필수 약관이 모두 True로 처리됐는지 검증
    private void validateRequiredTermsAgreed(Map<Long, Boolean> agreedMap) {
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

    // 회원가입 시 제출된 페르소나 답변 리스트로부터 최종 페르소나 계산
    private Persona resolvePersonaFromSignup(List<PersonaAnswerReqDTO> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_PERSONA_ANSWERS);
        }

        List<PersonaAnswerReqDTO> convertedAnswers = answers.stream()
                .map(a -> new PersonaAnswerReqDTO(a.getQuestionId(), a.getOptionId()))
                .toList();

        return personaScoringService.resolvePersona(convertedAnswers);
    }

    // agreedMap 기반으로 members-terms 매핑 테이블에 저장
    private void saveTermAgreements(Member member, Map<Long, Boolean> agreedMap) {
        List<Long> termIds = new ArrayList<>(agreedMap.keySet());

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