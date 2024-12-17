package com.ecom.security_service.util;

import com.ecom.security_service.controller.UserModel;
import com.ecom.security_service.enums.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.time.LocalTime;
import java.util.List;

public final class RequestUtil {
    public static void setUserSession(HttpSession session, UserModel user) {
        session.setAttribute("userId", user.id());
        session.setAttribute("userEmail", user.email());
        session.setAttribute("loggedIn", true);
        session.setAttribute("sessionValid", true);
        session.setAttribute("roles", List.of(UserRole.CUSTOMER));
        session.setAttribute("phoneNumber", user.phoneNumber());
        session.setAttribute("name", user.firstName() + " " + user.lastName());
    }

    public static Cookie createSessionCookie(HttpSession session) {
        Cookie cookie = new Cookie("sessionId", session.getId()); //springowy session id
        cookie.setHttpOnly(true);

        cookie.setDomain("localhost");
        cookie.setPath("/");
        return cookie;
    }

    public static void addSessionCookieToResponse(HttpServletResponse response, Cookie sessionCookie) {
        response.addCookie(sessionCookie);
    }
}
