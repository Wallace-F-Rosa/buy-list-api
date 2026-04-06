package com.project.buylist.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class JwtNonProdConfig {
    @Value("${spring.security.oauth2.jwt-secret}")
    public String jwtSecret;

    public String getJwtSecret() {
        return jwtSecret;
    }
}
