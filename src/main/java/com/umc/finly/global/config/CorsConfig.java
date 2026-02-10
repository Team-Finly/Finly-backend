package com.umc.finly.global.config;

import com.umc.finly.global.config.properties.AppCorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties(AppCorsProperties.class)
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppCorsProperties props) {
        CorsConfiguration config = new CorsConfiguration();

        // 허용 Origin (yml에서 관리)
        List<String> origins = props.originList();
        if (origins.contains("*")) {
            config.setAllowedOriginPatterns(List.of("*"));
        } else {
            config.setAllowedOrigins(origins);
        }

        // 허용 HTTP Method
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 허용 Header
        config.addAllowedHeader("*");

        // JWT를 Authorization 헤더로만 쓰면 false도 가능.
        // 쿠키/세션/리프레시토큰을 쿠키로 쓰면 true 필요.
        config.setAllowCredentials(true);

        // 프론트에서 읽어야 하는 응답 헤더가 있다면 노출
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        // preflight 캐시
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}