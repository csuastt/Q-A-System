package com.example.qa.user;

import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Page<User> findAllByRole(UserRole role, Pageable pageable);
}
