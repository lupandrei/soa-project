package com.example.user_service.service;

import com.example.user_service.dto.UserLoginRequest;
import com.example.user_service.dto.UserRegisterRequest;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private PasswordEncoder encoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public String login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email()).orElseThrow(() -> new RuntimeException("User not Found"));
        if (!encoder.matches(userLoginRequest.password(), user.getPassword()))
            throw new RuntimeException("Invalid password");
        return user.getEmail();
    }

    public void signUp(UserRegisterRequest userRegisterRequest) {
        if (!userRepository.findByEmail(userRegisterRequest.email()).isPresent()) {
            User user = new User();
            user.setEmail(userRegisterRequest.email());
            user.setPassword(encoder.encode(userRegisterRequest.password()));
            userRepository.save(user);
        }
    }
}
