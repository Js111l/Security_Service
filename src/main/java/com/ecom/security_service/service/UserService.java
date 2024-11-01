package com.ecom.security_service.service;

import com.ecom.security_service.controller.LoginRequestModel;
import com.ecom.security_service.controller.UserModel;
import com.ecom.security_service.dao.UserRepository;
import com.ecom.security_service.dao.entity.User;
import com.ecom.security_service.dao.mapper.UserMapper;
import com.ecom.security_service.enums.UserRole;
import com.ecom.security_service.model.UserSessionModel;
import com.ecom.security_service.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByEmail(username).orElseThrow());
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createNewUser(LoginRequestModel loginRequestModel) {
        return userRepository.save(getNewUser(loginRequestModel));
    }

    private User getNewUser(LoginRequestModel loginRequestModel) {
        var user = new User();
        user.setPassword(passwordEncoder.encode(loginRequestModel.password()));
        user.setEmail(loginRequestModel.email());
        user.setEmailConfirmed(false);
        user.setUserRole(UserRole.CUSTOMER);//todo
        return user;
    }

    public Optional<User> fetchById(Long id) {
        return this.userRepository.findById(id);
    }

    public UserModel getCurrentUser() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        return UserMapper.INSTANCE.entityToModel(
                this.userRepository.findByEmail(auth.getName()).orElseThrow()
        );
    }

    public String refreshToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var currentUser = this.getCurrentUser();
        var map = new HashMap<String, String>();
        map.put("role", currentUser.role().name());
        return new TokenUtil().createToken(
                map,
                currentUser.email(),
                currentUser.id().toString()
        );
    }

    public Boolean isLoggedIn(HttpServletRequest request, HttpSession session) throws JsonProcessingException {
        var cookies = request.getCookies();
        Cookie sessionCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    sessionCookie = cookie;
                }
            }
        }
        if (sessionCookie != null) {
            var user = (UserSessionModel) session.getAttribute(sessionCookie.getValue());
            return user.getLoggedIn();
        } else {
            //brak usera w sesji, brak sessionId w cookies
            return false;
        }
    }
}
