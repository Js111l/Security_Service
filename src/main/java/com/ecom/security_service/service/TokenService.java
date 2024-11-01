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

//    public String generateOneTimeToken(String intentId, String secret2) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
//        final var token = new TokenUtil().createOneTimeToken(intentId, secret);
//
//        var entity = new OneTimeToken();
//        entity.setToken(token);
//        entity.setUuid(encode(algo, intentId, secret)); //zahaszhowany intentId
//        entity.setUsed(false); //mark token as not used
//
//        return oneTimeTokenRepository.save(entity).getUuid();
//    }
//
//    public void verifyOneTimeToken(String uuid, String intentId) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
//        final var oneTimeToken = this.oneTimeTokenRepository.getByUuid(encode(algo, uuid, secret));
//        if (!oneTimeToken.isPresent()) {
//            throw new RuntimeException(uuid + "    " + 2137);
//        }
////        if (oneTimeToken.getUsed()) {
////            throw new RuntimeException("Already used!");
////        }
////        oneTimeToken.setUsed(true);
//        try {
//            new TokenUtil().verifyOneTimeToken(oneTimeToken.get().getToken(), secret);
//        } catch (Exception ex) {
//            throw new RuntimeException(intentId);
//        }
//    }

    public String getPaymentToken(String intentId, HttpSession session) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, JsonProcessingException {
        var sessionId = session.getId();
//        String attribute = session.getAttribute(sessionId);

//        UserSessionModel user = null;
//        if (attribute != null) {
//            user = (UserSessionModel) attribute;
//        }

        var encodedIntentId = encode(algo, intentId, secret);
        var value = encode(algo, encodedIntentId, sessionId);
//        if (user != null) {
//            value = encode(algo, value, user.getId());
//        }
        return new TokenUtil().createPaymentToken(value);
    }

}
