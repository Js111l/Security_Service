package com.ecom.security_service.service;

import com.ecom.security_service.model.UserSessionModel;
import com.ecom.security_service.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {
    private final UserService userService;
//    @Value("${jwt.secret}")
//    private String secret;
//    @Value("${jwt.salt}")
//    private String salt;

    private String secret = "hsef";
    private String algo = "HmacSHA512";

    private final TokenUtil tokenUtil;
    private final RedisSessionService redisSessionService;

    public static String encode(String algorithm, String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes()));
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getPaymentToken(String uuid, HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException {

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
        );
        var claims = new HashMap<String, Object>();
        claims.put("sessionId", sessionId);
        claims.put("userEmail", email);
        claims.put("loggedIn", loggedIn);
        claims.put("authorities", roles);
        claims.put("uuid", uuid);
        return tokenUtil.createPaymentToken(claims);
    }

}
