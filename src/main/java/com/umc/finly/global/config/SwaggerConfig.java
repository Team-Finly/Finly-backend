package com.umc.finly.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        Info info = new Info().title("Finly API 명세서").description("Finly의 SpringBoot 서버 API 명세서입니다.").version("0.0.1");

        // JWT 토큰 헤더 방식
        String securityScheme = "JWT TOKEN";
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityScheme);

        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(securityScheme, new SecurityScheme()
                        .name(securityScheme)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("/"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
