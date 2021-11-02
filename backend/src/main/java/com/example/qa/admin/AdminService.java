package com.example.qa.admin;

import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.security.SecurityConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        if (adminRepository.count() == 0) {
            Admin admin = new Admin(
                    new AdminRequest(
                            SecurityConstants.SUPER_ADMIN_USERNAME,
                            passwordEncoder.encode(SecurityConstants.SUPER_ADMIN_PASSWORD),
                            AdminRole.SUPER_ADMIN
                    )
            );
            save(admin);
        }
    }

    public void save(Admin admin) {
        adminRepository.save(admin);
    }

    public Page<Admin> listAll(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public Page<Admin> listByRole(AdminRole role, Pageable pageable) {
        return adminRepository.findAllByRole(role, pageable);
    }

    public Admin getById(long id, boolean allowDeleted) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        Admin admin = adminOptional.get();
        if (admin.isDeleted() && !allowDeleted) {
            throw new UsernameNotFoundException(null);
        }
        return admin;
    }

    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }

    public Admin getByUsername(String username) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        return adminOptional.get();
    }
}
