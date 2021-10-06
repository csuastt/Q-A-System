package com.example.qa.user.repository;

import com.example.qa.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

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

    /**
     * @param limit    Where to start
     * @param offset   Item to show
     * @return         A list of AppUser requested
     */
    @Query(value = "select * from AppUser limit ?1 offset ?2 ;", nativeQuery = true)
    List<AppUser> findAll(int limit, int offset);

    /**
     * @param limit   Where to start
     * @param offset  Item to show
     * @param permit  A questioner or A answerer
     * @return        A list of AppUser requested
     */
    @Query(value = "select * from AppUser where permit = ?3 limit ?1 offset ?2 ;", nativeQuery = true)
    List<AppUser> findAllByPermit(int limit, int offset, String permit);

    void deleteByUsername(String username);
}
