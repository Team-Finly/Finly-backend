package com.umc.finly.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {

    /**
     * HTTPS 환경에서만 true
     */
    private boolean secure = false;
    private String sameSite = "Lax";
    private String path = "/auth";
}
