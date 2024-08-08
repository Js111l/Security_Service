package com.ecom.security_service.controller;

import com.ecom.security_service.service.UserService;
import com.ecom.security_service.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;


@RestController
@Slf4j
public class AuthenticationRestController {
    //TODO

    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private UserService userService;

    private final TokenUtil tokenUtil = new TokenUtil();

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<String> login(@RequestBody LoginRequestModel loginRequestModel) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.manager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestModel.email(),
                loginRequestModel.password()
        ));
        log.debug("user successfully logged");
        return ResponseEntity.ok(
                tokenUtil.createToken(new HashMap<>(), loginRequestModel.email(), loginRequestModel.password())
        );
    }

//    @PostMapping("/register")
//    public String register(@RequestBody LoginRequestModel loginRequestModel) throws NoSuchAlgorithmException, InvalidKeySpecException {
//
//        final var user = userService.getByEmail(loginRequestModel.email());
//        if (user != null) {
//            throw new RuntimeException("Email already used!"); //TODO zmienic exceptions na lepsze
//        }
//
//    }


}












