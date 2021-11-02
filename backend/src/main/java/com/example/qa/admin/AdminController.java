package com.example.qa.admin;

import com.example.qa.admin.exchange.AdminListResponse;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.AdminResponse;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ChangePasswordRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        if (request.getRole() == AdminRole.SUPER_ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        if (request.getUsername().contains("@") || adminService.existsByUsername(request.getUsername())) {
            throw new ApiException(403, "USERNAME_INVALID");
        }
        if (request.getRole() == null) {
            request.setRole(AdminRole.ADMIN);
        }
        String password = RandomStringUtils.randomAlphanumeric(10);
        request.setPassword(passwordEncoder.encode(password));
        adminService.save(new Admin(request));
        return new PasswordResponse(password);
    }

    @GetMapping
    public AdminListResponse listAdmins(
            @RequestParam(required = false) AdminRole role,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.ADMIN_LIST_MAX_PAGE_SIZE);
        PageRequest pageRequest = PageRequest.ofSize(pageSize).withPage(page - 1);
        Page<Admin> result = role != null ? adminService.listByRole(role, pageRequest) : adminService.listAll(pageRequest);
        return new AdminListResponse(result);
    }

    @GetMapping("/{id}")
    public AdminResponse getAdmin(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authAdminOrSuperAdminOrThrow(id);
        Admin admin = getAdminOrThrow(id, authIsSuperAdmin());
        return new AdminResponse(admin);
    }

    @PutMapping("/{id}")
    public void editAdmin(@PathVariable(value = "id") long id, @RequestBody AdminRequest request) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        if (id == 1 || request.getRole() == AdminRole.SUPER_ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        Admin admin = getAdminOrThrow(id, false);
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
        Admin admin = getAdminOrThrow(id, false);
        if (!authIsSuperAdmin() && !passwordEncoder.matches(request.getOriginal(), admin.getPassword())) {
            throw new ApiException(403, "WRONG_PASSWORD");
        }
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminService.save(admin);
    }

    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        Admin admin = getAdminOrThrow(id, true);
        if (admin.isDeleted()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ALREADY_DELETED");
        }
        admin.setDeleted(true);
        admin.setUsername(admin.getUsername() + "@" + admin.getId());
        adminService.save(admin);
    }

    private Admin getAdminOrThrow(long id, boolean allowDeleted) {
        try {
            return adminService.getById(id, allowDeleted);
        } catch (UsernameNotFoundException e) {
            throw new ApiException(404);
        }
    }
}
