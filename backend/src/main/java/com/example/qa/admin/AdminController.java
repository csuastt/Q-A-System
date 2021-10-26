package com.example.qa.admin;

import com.example.qa.admin.exchange.AdminListResponse;
import com.example.qa.admin.exchange.CreateAdminRequest;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.security.SecurityConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.example.qa.security.RestControllerAuthUtils.authIsAdmin;
import static com.example.qa.security.RestControllerAuthUtils.authLogin;

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
    @ResponseStatus(HttpStatus.OK)
    public PasswordResponse createAdmin(@RequestBody CreateAdminRequest request) {
        if (request.getRole() == AdminRole.SUPER_ADMIN) {
            throw new ApiException(403);
        }
        if (request.getRole() == null) {
            request.setRole(AdminRole.ADMIN);
        }
        String password = SecurityConstants.SUPER_ADMIN_PASSWORD;
        request.setPassword(passwordEncoder.encode(password));
        adminService.save(new Admin(request));
        return new PasswordResponse(password);
    }

    @GetMapping
    public AdminListResponse listAdmins(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.ADMIN_LIST_MAX_PAGE_SIZE);
        PageRequest pageRequest = PageRequest.ofSize(pageSize).withPage(page - 1);
        Page<Admin> result = adminService.listAll(pageRequest);
        return new AdminListResponse(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void getAdmin(@PathVariable(value = "id") long id) {
        if (!authLogin()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED);
        }
        if (!authIsAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NO_PERMISSION");
        }
        Admin admin;
        try {
            admin = adminService.getById(id, true);
        } catch (UsernameNotFoundException e) {
            throw new ApiException(404);
        }
        if (admin.isDeleted()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ALREADY_DELETED");
        }
        admin.setDeleted(true);
        admin.setUsername(admin.getUsername() + "@" + admin.getId());
        adminService.save(admin);
    }
}
