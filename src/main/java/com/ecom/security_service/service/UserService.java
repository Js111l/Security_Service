package com.ecom.security_service.service;

import com.ecom.security_service.controller.LoginRequestModel;
import com.ecom.security_service.controller.UserModel;
import com.ecom.security_service.dao.UserRepository;
import com.ecom.security_service.dao.entity.User;
import com.ecom.security_service.dao.mapper.UserMapper;
import com.ecom.security_service.enums.UserRole;
import com.ecom.security_service.util.TokenUtil;
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
}
