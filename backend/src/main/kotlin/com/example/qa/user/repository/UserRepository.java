package com.example.qa.user.repository;

import com.example.qa.user.model.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<AppUser, Long> {
    @Query
    List<AppUser> findAllByPermit(String permit);
    @Query
    Optional<AppUser> findByUsername(String username);
    @Query
    boolean existsByUsername(String username);
//    @Query
//    List<AppUser>
}
