package com.umc.finly.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.global.apiPayload.response.ApiResponse;
import com.umc.finly.global.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
         * [동적 RequestMatcher]
         * - 같은 URI라도 query parameter(mode)에 따라 접근 권한을 다르게 주기 위함
         * - /api/persona-test/submit?mode=signup  → 인증 없이 허용
         * - /api/persona-test/submit?mode=retest → 로그인(JWT) 필요
         */
        RequestMatcher signupMatcher = request ->
                "/api/persona-test/submit".equals(request.getServletPath()) &&
                        "signup".equals(request.getParameter("mode"));

        RequestMatcher retestMatcher = request ->
                "/api/persona-test/submit".equals(request.getServletPath()) &&
                        "retest".equals(request.getParameter("mode"));

        /*
         * [인증/인가 실패 시 JSON 응답을 내려주기 위한 설정]
         * - AuthenticationEntryPoint : 인증 자체가 안 된 경우 (401)
         * - AccessDeniedHandler      : 인증은 됐지만 권한이 없는 경우 (403)
         * - 기본 HTML 응답 대신 ApiResponse 형태의 JSON으로 통일
         */
        ObjectMapper objectMapper = new ObjectMapper();

        AuthenticationEntryPoint entryPoint = (request, response, authException) -> {
            response.setStatus(AuthErrorCode.UNAUTHORIZED.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ApiResponse<Object> body = ApiResponse.onFailure(
                    AuthErrorCode.UNAUTHORIZED,
                    AuthErrorCode.UNAUTHORIZED.getMessage()
            );

            objectMapper.writeValue(response.getWriter(), body);
        };

        AccessDeniedHandler deniedHandler = (request, response, accessDeniedHandler) -> {
            response.setStatus(AuthErrorCode.FORBIDDEN.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ApiResponse<Object> body = ApiResponse.onFailure(
                    AuthErrorCode.FORBIDDEN,
                    AuthErrorCode.FORBIDDEN.getMessage()
            );

            objectMapper.writeValue(response.getWriter(), body);
        };

        http
                /** [기존 보안 기능 비활성화] **/
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                /** [URL 접근 권한 설정] **/
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC
                        .requestMatchers("/auth/**", "/api/persona-test/questions").permitAll()
                        .requestMatchers(signupMatcher).permitAll()

                        // PROTECTED
                        .requestMatchers("/api/mypage/**").authenticated()
                        .requestMatchers(retestMatcher).authenticated()

                        // 나머지
                        .anyRequest().permitAll()
                )
                /*
                 * [인증/인가 실패 시 처리 로직 연결]
                 * - 로그인 안 함 / 토큰 없음 / 만료 → 401 (AuthenticationEntryPoint)
                 * - 로그인 했지만 접근 권한 없음 → 403 (AccessDeniedHandler)
                 */
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler))
                /*
                 * [JWT 인증 필터 등록]
                 * - UsernamePasswordAuthenticationFilter 이전에 실행
                 * - Authorization: Bearer {token} 헤더에서 JWT 추출
                 * - 유효한 경우 SecurityContext에 AuthPrincipal 세팅
                 */
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
