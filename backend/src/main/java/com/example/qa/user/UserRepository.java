package com.example.qa.user;

import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByIdAndDeleted(long id, boolean deleted);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Page<User> findAllByRoleIn(Collection<User.Role> role, Pageable pageable);
}
