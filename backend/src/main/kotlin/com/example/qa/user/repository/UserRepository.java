package com.example.qa.user.repository;

import com.example.qa.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * @param aLong  定位id
     * @param enable 判断是否是activeUser
     * @return      是否存在
     */
    @Query
    boolean existsByIdAndEnable(Long aLong, boolean enable);

    /**
     * @param username Unique to specify a User
     * @return A user with requested username
     */
    @Query
    Optional<AppUser> findByUsernameAndEnable(String username, boolean enable);

    /**
     * @param username Unique to specify a User
     * @return Whether the user exists in the repository
     */
    @Query
    boolean existsByUsernameAndEnable(String username, Boolean enable);

    /**
     * @param limit    Where to start
     * @param offset   Item to show
     * @return         A list of AppUser requested
     */
    @Query(value = "select * from AppUser where enable = true limit ?1 offset ?2 ;", nativeQuery = true)
    List<AppUser> findAll(int limit, int offset);

    /**
     * @param limit   Where to start
     * @param offset  Item to show
     * @param permit  A questioner or A answerer
     * @return        A list of AppUser requested
     */
    @Query(value = "select * from AppUser where permit = ?3 and enable = true limit ?1 offset ?2 ;", nativeQuery = true)
    List<AppUser> findAllByPermit(int limit, int offset, String permit);

    /**
     * @param enable true/false
     * @return all activeUsers
     */
    @Query
    List<AppUser> findAllByEnable(boolean enable);

}
