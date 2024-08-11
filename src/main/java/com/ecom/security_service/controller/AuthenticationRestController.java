package com.ecom.security_service.controller;

import com.ecom.security_service.service.EmailService;
import com.ecom.security_service.service.UserService;
import com.ecom.security_service.service.RegistrationService;
import com.ecom.security_service.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;


@RestController //TODO rest exceptions handler
@Slf4j
public class AuthenticationRestController {
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private UserService userService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private EmailService emailService;

    private final TokenUtil tokenUtil = new TokenUtil();

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<String> login(@RequestBody final LoginRequestModel loginRequestModel) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.manager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestModel.email(),
                loginRequestModel.password()
        ));
        log.debug("user successfully logged");
        return ResponseEntity.ok(
                tokenUtil.createToken(new HashMap<>(), loginRequestModel.email(), loginRequestModel.password())
        );
    }

    @PostMapping("/register")
    @CrossOrigin
    public void register(@RequestBody final LoginRequestModel loginRequestModel) {
        userService.getByEmail(loginRequestModel.email()).ifPresent(x -> {
            throw new RuntimeException("Email already used!"); //TODO zmienic exceptions na lepsze
        });
        final var token = this.registrationService.createRegistrationTokenForUser(loginRequestModel);
        this.emailService.sendRegistrationConfirmationMail(token);
    }

    @GetMapping("/register/activation-token")
    public String verifyActivationToken(@RequestParam("token") final String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return this.registrationService.processRegistrationConfirmation(token);
    }


}












