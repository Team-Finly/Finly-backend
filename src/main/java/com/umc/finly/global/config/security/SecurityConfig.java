package com.umc.finly.global.config.security;

import com.umc.finly.global.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    // 인증/인가 규칙, 필터 체인 구성

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        // 로그인 없이 접근 허용 (PUBLIC APIs)
                        .requestMatchers(
                                "/api/persona-test/questions",
                                "/api/persona-test/submit?mode=signup",
                                "/api/auth/check-email",
                                "/api/persona-test/submit?mode=signup"

                        ).permitAll()
                        // JWT 필요 (PROTECTED APIs)
                        .requestMatchers("/api/persona-test/submit?mode=retest").authenticated()
                        // 그 외는 일단 허용 (추후 운영 단계에서 authenticated()로 점진 강화 예정)
                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
