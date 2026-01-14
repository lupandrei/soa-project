package com.example.user_service.security;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    private static final String COOKIE_NAME = "auth-cookie";

    private final JwtUtil jwtUtil;

    @Autowired
    public CookieService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public Cookie createCookie(String email) {
        String jwt = jwtUtil.generateToken(email);
        Cookie cookie = new Cookie(COOKIE_NAME, jwt);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        return cookie;
    }
}
