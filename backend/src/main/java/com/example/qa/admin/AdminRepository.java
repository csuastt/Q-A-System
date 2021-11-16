package com.example.qa.admin;

import com.example.qa.admin.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByUsername(String username);

    Optional<Admin> findByUsername(String username);

    Page<Admin> findAllByRoleIn(Collection<Admin.Role> role, Pageable pageable);
}
