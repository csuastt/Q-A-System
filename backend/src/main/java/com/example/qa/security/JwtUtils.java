package com.example.qa.security;

import com.example.qa.admin.model.Admin;
import com.example.qa.config.SystemConfig;
import com.example.qa.user.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

@Log4j2
public class JwtUtils {
    private JwtUtils() {
    }

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

    public static UserAuthentication getAuthentication(String token) {
        if (token != null && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            try {
                Jws<Claims> parsedToken = Jwts.parser()
                        .setSigningKey(SecurityConstants.JWT_SECRET.getBytes())
                        .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));

                long id = Long.parseLong(parsedToken.getBody().getSubject());
                String role = parsedToken.getBody().get(SecurityConstants.ROLE_CLAIM, String.class);

                if (role.equals(SecurityConstants.ROLE_USER)) {
                    return new UserAuthentication(id, User.class);
                } else if (role.equals(SecurityConstants.ROLE_ADMIN)) {
                    return new UserAuthentication(id, Admin.class);
                }
            } catch (ExpiredJwtException exception) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }
        return null;
    }
}
