package com.example.qa.user.repository;

import com.example.qa.user.model.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<AppUser, Long> {
    /**
     * @param permit Differ from q and a
     * @return A list of users with requested permission
     */
    @Query
    List<AppUser> findAllByPermit(String permit);

    /**
     * @param username Unique to specify a User
     * @return A user with requested username
     */
    @Query
    Optional<AppUser> findByUsername(String username);

    /**
     * @param username Unique to specify a User
     * @return Whether the user exists in the repository
     */
    @Query
    boolean existsByUsername(String username);
}
