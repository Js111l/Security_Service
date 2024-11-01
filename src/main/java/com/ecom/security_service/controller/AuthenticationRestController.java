package com.ecom.security_service.controller;

import com.ecom.security_service.model.LoginStatusResponse;
import com.ecom.security_service.model.TokenResponse;
import com.ecom.security_service.model.UserSessionModel;
import com.ecom.security_service.service.TokenService;
import com.ecom.security_service.service.UserService;
import com.ecom.security_service.service.RegistrationService;
import com.ecom.security_service.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

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

    private final TokenUtil tokenUtil = new TokenUtil();

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody final LoginRequestModel loginRequestModel, HttpServletResponse response, HttpSession session) throws JsonProcessingException {

        try {
            Authentication auth = this.manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestModel.email(),
                            loginRequestModel.password()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }

        final var sessionId = session.getId();

        Cookie cookie = new Cookie("sessionId", sessionId); //springowy session id
        var user = userService.getCurrentUser();
        session.setAttribute(sessionId,
                new UserSessionModel(
                        user.id().toString(),
                        user.email(),
                        true,
                        true
                        // Add roles if needed
                ));
        // a do cache klucz: session id,  user
        cookie.setHttpOnly(true);
        cookie.setMaxAge(LocalTime.of(0, 15).getMinute() * 60);

        response.addCookie(cookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(HttpServletResponse response, HttpSession session) {

        final var sessionId = session.getId();
        session.removeAttribute(sessionId);
        session.invalidate();

        Cookie cookie = new Cookie("sessionId", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/session/verify")
    public LoginStatusResponse verifyIfLoggedIN(HttpServletRequest request, HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {


        var isVerified = this.userService.isLoggedIn(request, session);
        //tu jakas walidacja czy user jest zalogowanym userem, czy zwyklym gościem. Narazie takie porownanie
        if (!isVerified) {
            //ifek
            // jesli nie zalogowany

            if (session.isNew()) {
                session.setAttribute(session.getId(),
                        new UserSessionModel(
                                session.getId(),
                                null,
                                true,
                                true
                                // Add roles if needed
                        ));
            }
            // docelowo jakis model usera, czy zalogowany, czy niezalogoway gość,
            // jakie ma role, podst. info. itd.
            // model bedzie podstawa w tworzeniu jwt, tam sie wstawi jakie uprawinienia ma user itd.

            return new LoginStatusResponse(false);
        } else {
            //jesli zalogowany

        }

        return new LoginStatusResponse(true);
    }

    @PostMapping("/session/jwt")
    public TokenResponse getJWT(HttpServletRequest request, HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        var cookies = request.getCookies();
//        Cookie sessionCookie = null;
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("sessionId")) {
//                    sessionCookie = cookie;
//                }
//            }
//        }
//        if (sessionCookie == null ||
//                Objects.equals(session.getAttribute("sessionId").toString(), sessionCookie.getValue())) {
//            throw new RuntimeException();
//        }

        return new TokenResponse(this.tokenUtil.createToken(new HashMap<>(),
                UUID.randomUUID().toString(),
                //sessionCookie.getValue(),
                "session hehe" //session.getAttribute("sessionId").toString()
        ));
    }


    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody final LoginRequestModel loginRequestModel) {
        userService.getByEmail(loginRequestModel.email()).ifPresent(x -> {
            throw new RuntimeException("Email already used!"); //TODO zmienic exceptions na lepsze
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

    //DEFAULT JWT
    @PostMapping("/token")
    public TokenResponse verifyToken(@RequestParam("issuer") String issuer, @RequestParam("issuer") String subject,
                                     HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //JWT DO KOMUNIKACJI Z SERWISAMI

        return new TokenResponse(
                this.tokenUtil.createToken(
                        new HashMap<>(),
                        subject,
                        issuer
                )
        );
    }


    //PAYMENT TOKENS

    @PostMapping("/payment-token")
    public ResponseEntity<HttpStatus> verifyPaymentToken(HttpSession session, HttpServletRequest request) throws
            NoSuchAlgorithmException, InvalidKeySpecException {
        var cookies = request.getCookies();
        Cookie tokenCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("paymentToken")) {
                    tokenCookie = cookie;
                }
            }
        }
        Cookie sessionCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    sessionCookie = cookie;
                }
            }
        }
        this.tokenUtil.verifyPaymentToken(
                tokenCookie == null ? null : tokenCookie.getValue(),
                session,
                sessionCookie == null ? null : sessionCookie.getValue()
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/payment-token")
    public ResponseEntity<HttpStatus> getPaymentToken(@RequestParam("intentId") String intentId,
                                                      HttpSession session,
                                                      HttpServletResponse response) throws
            NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException {
        var token = this.service.getPaymentToken(intentId, session);


        Cookie cookie = new Cookie("paymentToken", token); //springowy session id
        // a do cache klucz: session id,  user
        cookie.setHttpOnly(true);
        cookie.setMaxAge(LocalTime.of(0, 15).getMinute() * 60);
        response.addCookie(cookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}