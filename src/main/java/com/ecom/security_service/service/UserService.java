package com.ecom.security_service.service;

import com.ecom.security_service.controller.LoginRequestModel;
import com.ecom.security_service.dao.UserRepository;
import com.ecom.security_service.dao.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return user;
    }

    public Optional<User> fetchById(Long id) {
        return this.userRepository.findById(id);
    }
}
