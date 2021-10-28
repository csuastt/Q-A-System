package com.example.qa.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.lang.reflect.Type;
import java.util.Objects;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final long id;
    private final transient Type role;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthentication that = (UserAuthentication) o;
        return id == that.id && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role);
    }
}
