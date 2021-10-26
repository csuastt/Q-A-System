package com.example.qa.admin;

import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.TokenResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.errorhandling.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.example.qa.security.JwtUtils.adminToken;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthenticationController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AdminAuthenticationController(AdminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            throw new ApiException(400);
        }
        Admin admin;
        try {
            admin = adminService.getByUsername(loginRequest.getUsername());
        } catch (UsernameNotFoundException e) {
            logger.info("login: username '{}' not found", loginRequest.getUsername());
            throw new ApiException(403);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            logger.info("login: wrong password for admin '{}'", loginRequest.getUsername());
            throw new ApiException(403);
        }
        logger.info("login: login success for admin '{}'", loginRequest.getUsername());
        return new TokenResponse(adminToken(admin.getId()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout() {
        // 目前退出无需任何操作
    }
}
