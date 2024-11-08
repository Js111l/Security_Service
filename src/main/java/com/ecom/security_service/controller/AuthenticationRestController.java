package com.ecom.security_service.controller;

import com.ecom.security_service.exception.ApiExceptionType;
import com.ecom.security_service.exception.ApplicationRuntimeException;
import com.ecom.security_service.model.LoginStatusResponse;
import com.ecom.security_service.model.TokenResponse;
import com.ecom.security_service.model.UserDataModel;
import com.ecom.security_service.service.*;
import com.ecom.security_service.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalTime;
import java.util.*;

@RestController //TODO rest exceptions handler
@Slf4j
@RequestMapping("/auth")
public class AuthenticationRestController {
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private UserService userService;
    @Autowired
    private RegistrationService registrationService;
    // @Autowired
    // private EmailService emailService;
    @Autowired
    private TokenService service;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisSessionService redisSessionService;

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody final LoginRequestModel loginRequestModel,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        var session = request.getSession();
        try {
            Authentication auth = this.manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestModel.email(),
                            loginRequestModel.password()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(ApiExceptionType.FAILED_LOGIN);
        }
        this.sessionService.createUserSession(session, userService.getCurrentUser(), response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request, HttpServletResponse response) {
        this.sessionService.invalidateUserSession(request, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/session/verify")
    public LoginStatusResponse verifyIfLoggedIn(HttpServletRequest request,
                                                HttpServletResponse response) {
        var session = request.getSession(true);
        return this.redisSessionService.verifySession(response, session);
    }

    @PostMapping("/session/jwt")
    public TokenResponse getJWT(@RequestParam("userSessionId") String userSessionId) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        Map<String, Object> claims = this.sessionService.getJwtClaimsFromUserSession(userSessionId);
        return new TokenResponse(this.tokenUtil.createToken(
                claims,
                "",
                userSessionId
        ));
    }


    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody final LoginRequestModel loginRequestModel) {
        userService.getByEmail(loginRequestModel.email()).ifPresent(x -> {
            throw  new ApplicationRuntimeException(ApiExceptionType.EMAIL_ALREADY_USED);//TODO na service przerzucic

        });
//        final var token = this.registrationService.createRegistrationTokenForUser(loginRequestModel);
        //this.emailService.sendRegistraymetionConfirmationMail(token);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                loginRequestModel.email(),
                loginRequestModel.password()
        ));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/register/activation-token")
    public String verifyActivationToken(@RequestParam("token") final String token) throws
            NoSuchAlgorithmException, InvalidKeySpecException {
        return this.registrationService.processRegistrationConfirmation(token);
    }


    @GetMapping("/context/current-user")
    public UserModel getCurrentUserId() {
        return this.userService.getCurrentUser();
    }

    @GetMapping("/token")
    public String refreshToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return this.userService.refreshToken();
    }


    //PAYMENT TOKENS

    @PostMapping("/payment-token")
    public ResponseEntity<HttpStatus> verifyPaymentToken(
            HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException {

        var cookies = request.getCookies();

        Cookie paymentTokenCookie = WebUtils.getCookie(request, "paymentToken");
        Cookie sessionCookie = WebUtils.getCookie(request, "sessionId");


        HttpSession session = request.getSession();
        this.tokenUtil.verifyPaymentToken(
                paymentTokenCookie == null ? null : paymentTokenCookie.getValue(),
                session
                //TODO?
//                sessionCookie == null ? null : sessionCookie.getValue()
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/payment-token")
    public ResponseEntity<HttpStatus> getPaymentToken(@RequestParam("uuid") String uuid,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException {
        var session = request.getSession();
        var token = this.service.getPaymentToken(uuid, session);

        Cookie cookie = new Cookie("paymentToken", token);
        // a do cache klucz: session id,  user
        cookie.setHttpOnly(true);
        cookie.setMaxAge(LocalTime.of(0, 15).getMinute() * 60);
        response.addCookie(cookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/session/user")
    public UserDataModel getUserData(HttpServletRequest request) {
        var session = request.getSession();
        return this.userService.getUserData(session);
    }
}