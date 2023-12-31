package com.example.qa.admin;

import com.example.qa.admin.exchange.AdminListResponse;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.AdminResponse;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ChangePasswordRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.example.qa.security.RestControllerAuthUtils.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public PasswordResponse createAdmin(@RequestBody AdminRequest request) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        if (request.getRole() == Admin.Role.SUPER_ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        if (request.getUsername().contains("@") || adminService.existsByUsername(request.getUsername())) {
            throw new ApiException(403, "USERNAME_INVALID");
        }
        if (request.getRole() == null) {
            request.setRole(Admin.Role.ADMIN);
        }
        String password = RandomStringUtils.randomAlphanumeric(10);
        request.setPassword(passwordEncoder.encode(password));
        adminService.save(new Admin(request));
        return new PasswordResponse(password);
    }

    @GetMapping
    public AdminListResponse listAdmins(
            @RequestParam(required = false) List<Admin.Role> role,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.ADMIN_LIST_MAX_PAGE_SIZE);
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.Direction.ASC, "id");
        role = Objects.requireNonNullElse(role, Arrays.asList(Admin.Role.REVIEWER, Admin.Role.ADMIN));
        Page<Admin> result = adminService.listByRole(role, pageRequest);
        return new AdminListResponse(result);
    }

    @GetMapping("/{id}")
    public AdminResponse getAdmin(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authAdminOrSuperAdminOrThrow(id);
        Admin admin = getAdminOrThrow(id);
        return new AdminResponse(admin);
    }

    @PutMapping("/{id}")
    public void editAdmin(@PathVariable(value = "id") long id, @RequestBody AdminRequest request) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        if (id == 1 || request.getRole() == Admin.Role.SUPER_ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        Admin admin = getAdminOrThrow(id);
        if (request.getPassword() != null) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        admin.update(request);
        adminService.save(admin);
    }

    @PutMapping("/{id}/password")
    public void changePassword(@PathVariable(value = "id") long id, @RequestBody ChangePasswordRequest request) {
        authLoginOrThrow();
        authAdminOrSuperAdminOrThrow(id);
        Admin admin = getAdminOrThrow(id);
        if (!authIsSuperAdmin() && !passwordEncoder.matches(request.getOriginal(), admin.getPassword())) {
            throw new ApiException(403, "WRONG_PASSWORD");
        }
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminService.save(admin);
    }

    private Admin getAdminOrThrow(long id) {
        try {
            return adminService.getById(id);
        } catch (UsernameNotFoundException e) {
            throw new ApiException(404);
        }
    }
}
