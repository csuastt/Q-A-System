package com.example.qa.user;

import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(long id);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Transactional
    Page<User> findAllByRoleIn(Collection<User.Role> role, Pageable pageable);

    @Transactional
    Page<User> findAllByApplying(boolean applying, Pageable pageable);
}
