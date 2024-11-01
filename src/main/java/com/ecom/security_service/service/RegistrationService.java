package com.ecom.security_service.service;

import com.ecom.security_service.controller.LoginRequestModel;
import com.ecom.security_service.dao.VerificationTokenRepository;
import com.ecom.security_service.dao.entity.User;
import com.ecom.security_service.dao.entity.VerificationToken;
import com.ecom.security_service.util.TokenUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RegistrationService {
    //LINK DO KONFIRMACJI EMAILA - RACZEJ PROSTY JWT, PO CO TE CALLE DO BAZY I RECZNE USTAWIANIE EXPIRATION?
    private final VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserService userService;

    private VerificationToken createToken(User newUser) {
        var newToken = new VerificationToken();
        newToken.setUser(newUser);
        newToken.setUuid(UUID.randomUUID().toString());
        newToken.setExpirationDate(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)));
        return verificationTokenRepository.save(newToken);
    }

    public VerificationToken createRegistrationTokenForUser(LoginRequestModel loginRequestModel) {
        final var newUser = this.userService.createNewUser(loginRequestModel);
        return this.createToken(newUser);
    }

    private VerificationToken getTokenByUUID(String uuid) {
        var token = this.verificationTokenRepository.findTokenByUUID(uuid).orElseThrow(
                () -> new RuntimeException("No token")
        );

        if (token.getExpirationDate().before(Date.from(Instant.now()))) {
            throw new RuntimeException(
                    "Link expired!"
            );
        }
        var user = this.userService.fetchById(token.getUser().getId()).orElseThrow();
        user.setEmailConfirmed(true);
        //TODO
        return token;
    }

    private void authenticateUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
    }

    public String processRegistrationConfirmation(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final var verificationToken = this.getTokenByUUID(token);
        final var user = verificationToken.getUser();
        this.authenticateUser(user);
        return new TokenUtil().createToken(
                new HashMap<>(),
                user.getEmail(),
                String.valueOf(user.getId())
        );
    }
}