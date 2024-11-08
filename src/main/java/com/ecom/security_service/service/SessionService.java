package com.ecom.security_service.service;

import com.ecom.security_service.controller.UserModel;
import com.ecom.security_service.util.RequestUtil;
import jakarta.persistence.Access;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class SessionService {
    private final RedisSessionService redisSessionService;
    private static final String SPRING_SESSION_PREFIX = "spring:session:sessions:";
    private static final String SESSION_ATTRIBUTE = "sessionAttr:";

    private static final String CLAIM_USER_ROLES = "roles";
    private static final String CLAIM_SESSION_ID = "sessionId";
    private static final String CLAIM_USER_EMAIL = "userEmail";
    private static final String CLAIM_USER_PHONE_NUMBER = "phoneNumber";
    private static final String CLAIM_USER_LOGGED_IN = "loggedIn";
    private static final String CLAIM_USER_NAME = "name";
    private static final String CLAIM_USER_USER_ID = "userId";

    public void invalidateUserSession(HttpServletRequest request, HttpServletResponse response) {
        var session = request.getSession();
        final var sessionId = session.getId();
        session.removeAttribute(sessionId);
        session.invalidate();

        Cookie cookie = new Cookie("sessionId", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void createUserSession(HttpSession session, UserModel currentUser, HttpServletResponse response) {
        RequestUtil.setUserSession(session, currentUser);
        Cookie cookie = RequestUtil.createSessionCookie(session);
        RequestUtil.addSessionCookieToResponse(response, cookie);
    }

    private Object getSessionAttribute(String sessionKey, String attribute) {
        return this.redisSessionService.getAttributeFromSession(sessionKey, attribute);
    }

    public Map<String, Object> getJwtClaimsFromUserSession(String userSessionId) {
        final String sessionKey = SPRING_SESSION_PREFIX + userSessionId;
        final Boolean isValid = (Boolean) this.redisSessionService.getAttributeFromSession(
                sessionKey,
                SESSION_ATTRIBUTE + "sessionValid"
        );
        if (isValid == null || !isValid) {
            throw new RuntimeException("FORBIDDEN BO SESJA NIEWAZNA");
        }
        final var claims = new HashMap<String, Object>();
        claims.put(CLAIM_USER_ROLES, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_USER_ROLES));
        claims.put(CLAIM_SESSION_ID, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_SESSION_ID));
        claims.put(CLAIM_USER_EMAIL, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_USER_EMAIL));
        claims.put(CLAIM_USER_PHONE_NUMBER, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_USER_PHONE_NUMBER));
        claims.put(CLAIM_USER_LOGGED_IN, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_USER_LOGGED_IN));
        claims.put(CLAIM_USER_NAME, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_USER_NAME));
        claims.put(CLAIM_USER_USER_ID, this.getSessionAttribute(sessionKey, SESSION_ATTRIBUTE + CLAIM_USER_USER_ID));
        return claims;
    }
    //TODO tu wrzucic te wszystkie operacje z sesjami i wyciaganiem z redisa
}
