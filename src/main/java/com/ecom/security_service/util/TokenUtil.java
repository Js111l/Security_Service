package com.ecom.security_service.util;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpSession;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
//    @Value("${jwt.secret}")
//    private String secret;
//    @Value("${jwt.salt}")
//    private String salt;

    String secret = "hsef";
    String salt = "fsj98";
    private static final Integer ITERATION_COUNT = 10_000;
    private static final Integer KEY_SIZE = 256;

    public String createToken(Map<String, String> claims, String subject, String issuer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(getTokenExpirationDate(Instant.now()))
                .issuer(issuer)
                .signWith(getKey())
                .compact();
    }

    public String createPaymentToken(String data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .claims(new HashMap<>())
                .subject(data)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
                //.issuer(userId)
                .signWith(getKey())
                .compact();
    }

    private Key getKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_SIZE);
        SecretKey pbeKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec);
        return new SecretKeySpec(pbeKey.getEncoded(), "HmacSHA256");
    }

    private Key getKeyFromInput(String secret) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_SIZE);
        SecretKey pbeKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec);
        return new SecretKeySpec(pbeKey.getEncoded(), "HmacSHA256");
    }

    private Date getTokenExpirationDate(Instant now) {
        return Date.from(now.plus(60, ChronoUnit.MINUTES));
    }

    public void verifyPaymentToken(String token,
                                   HttpSession session,
                                   String userSessionId) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        final var sessonId = session.getAttribute("sessionId");
//        if (sessonId != null && sessonId.equals(userSessionId)) {
        Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token);
//        } else {
//            throw new RuntimeException("UNAUTHORIZED");
//        }
    }

    public void verifyToken(String token) {
        new TokenUtil().verifyToken(token);
    }


}
