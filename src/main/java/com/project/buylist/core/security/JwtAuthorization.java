package com.project.buylist.core.security;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class JwtAuthorization {

    @Autowired
    private JwtNonProdConfig JwtNonProdConfig;

    @Bean
    @Profile("prod")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csfr -> csfr.disable())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/docs/**")
                        .permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Profile("!prod")
    public SecurityFilterChain filterChainDev(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/docs/**")
                        .permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }

    @Bean
    @Profile("!prod")
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(JwtNonProdConfig.getJwtSecret().getBytes(), 0,
                JwtNonProdConfig.getJwtSecret().getBytes().length,
                "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}