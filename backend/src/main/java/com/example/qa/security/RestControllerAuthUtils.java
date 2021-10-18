package com.example.qa.security;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.manager.model.AppManager;
import com.example.qa.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class RestControllerAuthUtils {
    private RestControllerAuthUtils() {
    }

    public static boolean authLogin() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    public static boolean authIsUser(long id) {
        UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getRole() == User.class && (long) auth.getPrincipal() == id;
    }

    public static boolean authIsAdmin() {
        UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getRole() == AppManager.class;
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
        if (!authIsUser(id) && !authIsAdmin()) {
            throw new ApiException(403, "NO_PERMISSION");
        }
    }
}
