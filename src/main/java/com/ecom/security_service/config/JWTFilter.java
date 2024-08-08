package com.ecom.security_service.config;

import com.ecom.security_service.service.UserService;
import com.ecom.security_service.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var tokenUtil = new TokenUtil();
        final var token = request.getHeader("Authorization");

        if (token != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                tokenUtil.verifyToken(token);
                final var userName = tokenUtil.getUserNameFromToken(token);
                final var user = userService.loadUserByUsername(userName);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                ));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
