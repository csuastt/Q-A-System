package com.example.qa.manager.repository;

import com.example.qa.manager.model.AppManager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ManagerRepository extends CrudRepository<AppManager, Long> {
    @Query
    List<AppManager> findAllByPermission(String permission);
    @Query
    Optional<AppManager> findByManagername(String managername);
    @Query
    boolean existsByManagername(String managername);

}
