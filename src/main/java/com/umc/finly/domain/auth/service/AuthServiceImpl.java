package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.req.AuthLoginReqDTO;
import com.umc.finly.domain.auth.dto.req.AuthSignUpReqDTO;
import com.umc.finly.domain.auth.dto.res.AuthLoginResDTO;
import com.umc.finly.domain.auth.dto.res.AuthSignUpResDTO;
import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.entity.mapping.MemberTerm;
import com.umc.finly.domain.auth.enums.TermType;
import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.auth.repository.TermRepository;
import com.umc.finly.domain.member.dto.request.PersonaAnswerReqDTO;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.domain.member.repository.MemberRepository;
import com.umc.finly.domain.member.repository.MemberTermRepository;
import com.umc.finly.domain.member.service.PersonaScoringService;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.infra.jwt.JwtProvider;
import com.umc.finly.global.util.CookieUtil;
import com.umc.finly.global.util.PasswordPolicy;
import com.umc.finly.global.util.SecurityUtil;
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
    private final MemberPersonaResultRepository memberPersonaResultRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 닉네임 양식
    private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,}$";

    // 필수 약관 정의
    private static final EnumSet<TermType> REQUIRED_TERMS =
            EnumSet.of(TermType.TERMS_AGREED, TermType.PRIVACY_AGREED);
    private final CookieUtil cookieUtil;

    /** 이메일 중복 확인 **/
    @Override
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    /** 회원가입 **/
    @Override
    public AuthSignUpResDTO signup(AuthSignUpReqDTO request){
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

        return AuthSignUpResDTO.builder()
                .memberId(savedMember.getId())
                .email(savedMember.getEmail())
                .nickname(savedMember.getNickname())
                .personaId(persona.getId())
                .build();
    }

    /** 로그인 **/
    @Override
    public LoginTokens login(AuthLoginReqDTO request){
        // 멤버 매칭
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_LOGIN_PASSWORD));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new CustomException(AuthErrorCode.INVALID_LOGIN_PASSWORD);
        }

        // 토큰 발급
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());

        LocalDateTime refreshExpiredAt = LocalDateTime.ofInstant(
                jwtProvider.getExpiration(refreshToken).toInstant(),
                ZoneId.systemDefault()
        );

        member.updateRefreshToken(refreshToken, refreshExpiredAt);
        memberRepository.save(member);

        AuthLoginResDTO result = AuthLoginResDTO.builder()
                .accessToken(accessToken)
                .member(AuthLoginResDTO.MemberInfo.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickname())
                        .build())
                .build();

        long refreshMaxAgeSeconds = Math.max(
                0,
                (jwtProvider.getExpiration(refreshToken).getTime() - System.currentTimeMillis())/1000
        );

        return new LoginTokens(result, refreshToken, refreshMaxAgeSeconds);
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

        // 1) refresh 토큰 1차 검증 (서명/만료/type=refresh) + 만료/무효 매핑
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

        // 6) CAS로 refreshToken 회전 (경쟁 조건 차단)
        int updated = memberRepository.rotateRefreshToken(
                memberId,
                rawRefreshToken,     // old token
                newRefreshToken,     // new token
                newRefreshExpiredAt
        );

        if (updated == 0) {
            // 동시에 다른 요청이 먼저 회전시켰거나, 서버 저장 토큰과 불일치
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
    public void logout(HttpServletResponse response){
        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(AuthErrorCode.INVALID_ACCESS_TOKEN));

        member.clearRefreshToken();
        cookieUtil.clearRefreshTokenCookie(response);
    }

    // -----------------------------------------------------------
    private boolean isValidPassword(String raw) {
        return PasswordPolicy.isValid(raw);
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null && nickname.matches(NICKNAME_REGEX);
    }

    private Map<Long, Boolean> toAgreedMap(List<AuthSignUpReqDTO.TermAgreementReq> agreements) {
        if (agreements == null || agreements.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_TERM_REQUEST);
        }

        // termId 중복 요청 방지 겸 정규화
        return agreements.stream().collect(Collectors.toMap(
                AuthSignUpReqDTO.TermAgreementReq::getTermId,
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

    private Persona resolvePersonaFromSignup(List<PersonaAnswerReqDTO> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_PERSONA_ANSWERS);
        }

        List<PersonaAnswerReqDTO> convertedAnswers = answers.stream()
                .map(a -> new PersonaAnswerReqDTO(
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