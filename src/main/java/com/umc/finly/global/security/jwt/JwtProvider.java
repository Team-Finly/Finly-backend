package com.umc.finly.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

    public String createAccessToken(Long memberId, String email) {
        return buildToken(memberId, email, "access", accessTokenExpireMs);
    }

    public String createRefreshToken(Long memberId, String email) {
        return buildToken(memberId, email, "refresh", refreshTokenExpireMs);
    }

    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String getType(String token) {
        return parseClaims(token).get("type", String.class);
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

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public void assertRefreshToken(String token) throws ExpiredJwtException, JwtException {
        Claims claims = parseClaims(token); // 여기서 만료/무효 예외 그대로 올라감
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new JwtException("Not a refresh token");
        }
    }

    private String buildToken(Long memberId, String email, String type, long expireMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("email", email)
                .claim("type", type)
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

        String raw = token.trim();
        while (raw.startsWith("Bearer ")) {
            raw = raw.substring(7).trim();
        }
        return raw;
    }

    public String normalize(String token) {
        return resolveToken(token);
    }

}
