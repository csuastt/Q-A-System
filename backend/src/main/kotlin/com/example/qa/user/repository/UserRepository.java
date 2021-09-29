package com.example.qa.user.repository;

import com.example.qa.user.model.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface UserRepository extends CrudRepository<AppUser, String> {
    @Query
    List<AppUser> findAllByPermit(String permit);
}
