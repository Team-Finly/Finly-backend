package com.umc.finly.global.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    // 토큰 생성 / 파싱 / 검증

    private final SecretKey secretKey;
    private final long accessTokenExpireMs;
    private final long refreshTokenExpireMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire-ms}") long accessTokenExpireMs,
            @Value("${jwt.refresh-token-expire-ms}") long refreshTokenExpireMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpireMs = accessTokenExpireMs;
        this.refreshTokenExpireMs = refreshTokenExpireMs;
    }

    // token 생성
    public String createAccessToken(Long memberId, String email) {
        return buildToken(memberId, email, "access", accessTokenExpireMs);
    }

    public String createRefreshToken(Long memberId, String email){
        return buildToken(memberId, email, "refresh", refreshTokenExpireMs);
    }

    // 토큰에서 추출
    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }
    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    // 서명/만료 등 기본 검증
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return "access".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 만료시간 변환 (디버깅/로직에 사용)
    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    private String buildToken(Long memberId, String email, String type, long expireMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("email", email)
                .claim("type", type)    // access | refresh
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        String raw = resolveToken(token);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(raw)
                .getPayload();
    }

    private String resolveToken(String token) {
        if (token == null) return null;
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
