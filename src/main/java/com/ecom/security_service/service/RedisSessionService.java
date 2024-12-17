package com.ecom.security_service.service;

import com.ecom.security_service.enums.UserRole;
import com.ecom.security_service.model.LoginStatusResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class RedisSessionService {
    private final JedisConfig jedisConfig;
    @Autowired
    private UserService userService;

    public LoginStatusResponse verifySession2(String userSessionId, HttpServletResponse response) {
        Boolean isValid = (Boolean) this.getAttributeFromSession(
                "spring:session:sessions:" + userSessionId,
                "sessionAttr:sessionValid"
        );
        if (isValid == null || !isValid) {
            throw new RuntimeException("FORBIDDEN BO SESJA NIEWAZNA");
        }

        var roles = this.getAttributeFromSession(
                "spring:session:sessions:" + userSessionId,
                "sessionAttr:roles"
        );
        var email = this.getAttributeFromSession(
                "spring:session:sessions:" + userSessionId,
                "sessionAttr:userEmail"
        );
        var loggedIn = (Boolean) this.getAttributeFromSession(
                "spring:session:sessions:" + userSessionId,
                "sessionAttr:loggedIn"
        );
        var userId = this.getAttributeFromSession(
                "spring:session:sessions:" + userSessionId,
                "sessionAttr:userId"
        );

        Cookie cookie = new Cookie("sessionId", userSessionId); //springowy session id
        cookie.setHttpOnly(true);

        cookie.setDomain("localhost");  // Set the domain to localhost
        cookie.setPath("/");
        response.addCookie(cookie);
        return new LoginStatusResponse(loggedIn, userSessionId);
    }

    public LoginStatusResponse verifySession(HttpServletResponse response, HttpSession session) {
        var isVerified = this.userService.isLoggedIn(session);
        //tu jakas walidacja czy user jest zalogowanym userem, czy zwyklym gościem. Narazie takie porownanie
        if (!isVerified) {
            //ifek
            // jesli nie zalogowany
            session.setAttribute("userId", "");
            session.setAttribute("userEmail", "");
            session.setAttribute("loggedIn", false);
            session.setAttribute("sessionValid", true);
            session.setAttribute("phoneNumber", "");
            session.setAttribute("roles", List.of(UserRole.GUEST_CUSTOMER));
            //dodaj do aktualnej sesji info o obecnym userze.

            Cookie cookie = new Cookie("sessionId", session.getId());
            cookie.setHttpOnly(true);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            response.addCookie(cookie);

            // docelowo jakis model usera, czy zalogowany, czy niezalogoway gość,
            // jakie ma role, podst. info. itd.
            // model bedzie podstawa w tworzeniu jwt, tam sie wstawi jakie uprawinienia ma user itd.

            return new LoginStatusResponse(false, session.getId());
        }
        //jesli zalogowany to nic nie rob, info juz zapisane o userze

        Cookie cookie = new Cookie("sessionId", session.getId());
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
        return new LoginStatusResponse(true, session.getId());
    }

    public Object getAttributeFromSession(String sessionKey, String attributeKey) {
        try (Jedis jedis = new Jedis(jedisConfig.host(), jedisConfig.port())) {
            Map<byte[], byte[]> hashData = jedis.hgetAll(sessionKey.getBytes());
            return !hashData.isEmpty() ? readValue(hashData.get(attributeKey.getBytes())) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private Object readValue(byte[] value) throws RuntimeException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(value))
        ) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
