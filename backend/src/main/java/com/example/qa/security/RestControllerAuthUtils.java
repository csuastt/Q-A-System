package com.example.qa.security;

import com.example.qa.admin.model.Admin;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class RestControllerAuthUtils {
    private RestControllerAuthUtils() {
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean authLogin() {
        return getAuthentication() != null && getAuthentication().getClass() == UserAuthentication.class;
    }

    public static boolean authIsUser() {
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == User.class;
    }

    public static boolean authIsUser(long id) {
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == User.class && (long) auth.getPrincipal() == id;
    }

    public static long authGetId() {
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return (long) auth.getPrincipal();
    }

    public static boolean authIsAdmin() {
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == Admin.class;
    }

    public static boolean authIsAdmin(long id) {
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == Admin.class && (long) auth.getPrincipal() == id;
    }

    public static boolean authIsSuperAdmin() {
        return authIsAdmin(1);
    }

    public static void authLoginOrThrow() {
        if (!authLogin()) {
            throw new ApiException(401);
        }
    }

    public static void authAdminOrThrow() {
        if (!authIsAdmin()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
    }

    public static void authUserOrThrow(long id) {
        if (!authIsUser(id)) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
    }

    public static void authSuperAdminOrThrow() {
        if (!authIsSuperAdmin()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
    }

    public static void authUserOrAdminOrThrow(long id) {
        if (!authIsUser(id) && !authIsAdmin()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
    }

    public static void authUserOrSuperAdminOrThrow(long id) {
        if (!authIsUser(id) && !authIsSuperAdmin()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
    }

    public static void authAdminOrSuperAdminOrThrow(long id) {
        if (!authIsAdmin(id) && !authIsSuperAdmin()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
    }
}
