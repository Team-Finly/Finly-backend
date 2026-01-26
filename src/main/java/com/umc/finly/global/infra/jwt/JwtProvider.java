package com.umc.finly.global.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    // 토큰 생성 / 파싱 / 검증

    private final Key key;
    private final long accessToeknExpireMs;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                       @Value("${jwt.access-token-expire-ms}") long accessTokenExpireMs
    ){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessToeknExpireMs = accessTokenExpireMs;
    }

    public String createAccessToken(Long memberId, String email, long expiresMs){
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessToeknExpireMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getMemberId(String token){
        return parseClaims(token).get("memberId", Long.class);
    }

    public boolean validate(String token){
        try{
            parseClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
