package com.example.qa.security;

import com.example.qa.config.SystemConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {

    private static String generateToken(String subject, String role, long expiration) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET.getBytes())
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim(SecurityConstants.ROLE_CLAIM, role)
                .compact();
    }

    public static String userToken(long id) {
        return generateToken(String.valueOf(id), SecurityConstants.ROLE_USER, SystemConfig.USER_TOKEN_EXPIRATION_MILLISECONDS);
    }

    public static String adminToken(long id) {
        return generateToken(String.valueOf(id), SecurityConstants.ROLE_ADMIN, SystemConfig.ADMIN_TOKEN_EXPIRATION_MILLISECONDS);
    }
}
