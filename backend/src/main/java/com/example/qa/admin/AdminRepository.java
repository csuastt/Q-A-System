package com.example.qa.admin;

import com.example.qa.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Optional<Admin> findByUsername(String username);
}
