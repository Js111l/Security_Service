package com.ecom.security_service;

import com.ecom.security_service.util.TokenUtil;
import org.antlr.v4.runtime.Token;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

@SpringBootApplication
@EnableWebSecurity
public class SecurityServiceApplication {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SpringApplication.run(SecurityServiceApplication.class, args);
    }

}
