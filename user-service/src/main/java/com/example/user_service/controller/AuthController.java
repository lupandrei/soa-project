package com.example.user_service.controller;

import com.example.user_service.dto.UserLoginRequest;
import com.example.user_service.dto.UserRegisterRequest;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.CookieService;
import com.example.user_service.security.JwtUtil;
import com.example.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private PasswordEncoder encoder;

//    @Autowired
//    private UserEventPublisher publisher;

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    private UserKafkaProducer kafkaProducer;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        authService.signUp(userRegisterRequest);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse httpServletResponse) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(authService.login(userLoginRequest), userLoginRequest.password()));
        return ResponseEntity.ok(jwtUtil.generateToken(userLoginRequest.email()));
    }

    @Value("${server.port}")
    private String port;

    @GetMapping("/whoami")
    public String whoAmI() {
        return "Handled by port " + port;
    }
}
