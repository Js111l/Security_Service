package com.ecom.security_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public JWTFilter jwtFilter(){
        return new JWTFilter();
    }
    @Bean
    public AuthenticationEntryPoint entryPoint(){
        return new SecurityEntryPoint();
    }

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        http.csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/register").permitAll()
                        .anyRequest().authenticated()
                ).addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(x->x.authenticationEntryPoint(entryPoint()));


        return http.build();
    }


}
