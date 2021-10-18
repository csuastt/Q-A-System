package com.example.qa.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.lang.reflect.Type;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final long id;
    private final Type role;

    public UserAuthentication(long id, Type role) {
        super(null);
        this.id = id;
        this.role = role;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return id;
    }

    public Type getRole() {
        return role;
    }
}
