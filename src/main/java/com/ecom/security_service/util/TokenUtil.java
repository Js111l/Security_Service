package com.ecom.security_service.util;

import com.ecom.security_service.client.FinancialTransactionsServiceClient;
import com.ecom.security_service.service.RedisSessionService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

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
import java.util.Map;

@Component
public class TokenUtil {
//    @Value("${jwt.secret}")
//    private String secret;
//    @Value("${jwt.salt}")
//    private String salt;

    String secret = "hsef";
    String salt = "fsj98";
    private static final Integer ITERATION_COUNT = 10_000;
    private static final Integer KEY_SIZE = 256;

    private final FinancialTransactionsServiceClient client;
    private final RedisSessionService redisSessionService;

    public TokenUtil(FinancialTransactionsServiceClient client, RedisSessionService redisSessionService) {
        this.client = client;
        this.redisSessionService = redisSessionService;
    }

    public String createToken(Map<String, Object> claims, String subject, String issuer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(getTokenExpirationDate(Instant.now()))
                .issuer(issuer)
                .signWith(getKey())
                .compact();
    }

    public String createPaymentToken(Map<String, Object> claims) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .claims(claims)
                .subject("API_GATEWAY_SUB")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
                .issuer("API_GATEWAY")
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

    public void verifyPaymentToken(String token, HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var sessionId = session.getId();
        boolean isValid = (boolean) this.redisSessionService.getAttributeFromSession(
                "spring:session:sessions:" + sessionId,
                "sessionAttr:sessionValid"
        );
        if (!isValid) {
            throw new RuntimeException("FORBIDDEN BO SESJA NIEWAZNA");
        }
        var roles = this.redisSessionService.getAttributeFromSession(
                "spring:session:sessions:" + sessionId,
                "sessionAttr:roles"
        );
        var email = this.redisSessionService.getAttributeFromSession(
                "spring:session:sessions:" + sessionId,
                "sessionAttr:userEmail"
        );
        var loggedIn = this.redisSessionService.getAttributeFromSession(
                "spring:session:sessions:" + sessionId,
                "sessionAttr:loggedIn"
        );//validate user session

        if (roles != null) {
            var parseSignedClaims = Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            var uuuid = parseSignedClaims.get("uuid", String.class);
            final var paymentDetails = client.getPaymentDetails(uuuid);
//            if (!paymentDetails.userId().toString().equals(user.getId())) {
//                throw new RuntimeException("Unmatched userId");
//            }
        } else {
            throw new RuntimeException("User is null!"); //if no session saved for this id -> exception
        }
    }

}
