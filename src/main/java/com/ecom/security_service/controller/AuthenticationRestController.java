package com.ecom.security_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class AuthenticationRestController {
 //TODO

    @Autowired
    private AuthenticationManager manager;



    @GetMapping("/login")
    public String login(LoginRequestModel loginRequestModel){
        return ""; //TODO
    }
}












