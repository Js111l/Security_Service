package com.ecom.security_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;


@SpringBootApplication
@EnableWebSecurity
@EnableFeignClients
public class SecurityServiceApplication {
    public static void main(String[] args) {
        System.out.println(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode(
                "elo"
        ));
        System.out.println(2137);
        SpringApplication.run(SecurityServiceApplication.class, args);
    }

}
