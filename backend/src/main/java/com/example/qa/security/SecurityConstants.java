package com.example.qa.security;

public final class SecurityConstants {
    private SecurityConstants() {
    }

    public static final String AUTH_LOGIN_URL = "/api/user/login";

    // Signing key for HS512 algorithm
    public static final String JWT_SECRET = "n2r5u8x/A%D*G-KaPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq4t7w!z%C*F-J@NcRf";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String ROLE_CLAIM = "rol";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";

    public static final String SUPER_ADMIN_USERNAME = "admin";
    public static final String SUPER_ADMIN_PASSWORD = "password";
}
