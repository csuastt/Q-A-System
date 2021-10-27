package com.example.qa.security;

import com.example.qa.admin.model.Admin;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RestControllerAuthUtils {
    private RestControllerAuthUtils() {
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean authLogin() {
        return getAuthentication() != null && getAuthentication().getClass() == UserAuthentication.class;
    }

    public static boolean authIsUser() {
        if (!authLogin()) {
            return false;
        }
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == User.class;
    }

    public static boolean authIsUser(long id) {
        if (!authLogin()) {
            return false;
        }
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == User.class && (long) auth.getPrincipal() == id;
    }

    public static long authGetUserId() {
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return (long) auth.getPrincipal();
    }

    public static boolean authIsAdmin() {
        if (!authLogin()) {
            return false;
        }
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == Admin.class;
    }

    public static boolean authIsAdmin(long id) {
        if (!authLogin()) {
            return false;
        }
        UserAuthentication auth = (UserAuthentication) getAuthentication();
        return auth.getRole() == Admin.class;
    }

    public static void authLoginOrThrow() {
        if (!authLogin()) {
            throw new ApiException(401);
        }
    }

    public static void authUserOrThrow(long id) {
        if (!authIsUser(id)) {
            throw new ApiException(403, "NO_PERMISSION");
        }
    }

    public static void authUserOrAdminOrThrow(long id) {
        if (!authIsUser(id)) {
            throw new ApiException(403, "NO_PERMISSION");
        }
    }
}
