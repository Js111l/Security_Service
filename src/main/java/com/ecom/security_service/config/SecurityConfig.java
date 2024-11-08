package com.ecom.security_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
public class SecurityConfig {
    @Bean
    public AuthenticationEntryPoint entryPoint() {
        return new SecurityEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/login",
                                "/auth/session/jwt",
                                "/auth/register",
                                "/auth/register/activation-token",
                                "/auth/token",
                                "/auth/payment-token",
                                "/auth/one-time-token",
                                "/auth/pay-token",
                                "/auth/session/verify",
                                "/auth/session/user")
                        .permitAll()
                        .anyRequest().permitAll()
                ).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER)
                )
                .requestCache(x->x.requestCache(new NullRequestCache()))
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(x -> x.authenticationEntryPoint(entryPoint()))
        ;
        return http.build();
    }

}
