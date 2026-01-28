package com.umc.finly.global.config.security;

import com.umc.finly.global.infra.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 요청마다 JWT 검사

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws java.io.IOException, jakarta.servlet.ServletException{

        // 이미 인증이 세팅되어 있으면 스킵 (중복 세팅 방지)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader= request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            if(jwtProvider.validateAccessToken(token)) {
                try {
                    Long memberId = jwtProvider.getMemberId(token);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    memberId, null, null
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception ignored) {
                    // 파싱/형변환 등 예외 나면 인증 세팅 없이 통과
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
