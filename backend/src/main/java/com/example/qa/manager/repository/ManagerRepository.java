package com.example.qa.manager.repository;

import com.example.qa.manager.model.AppManager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;


public interface ManagerRepository extends CrudRepository<AppManager, Long> {
    @Query
    Optional<AppManager> findByManagerName(String managerName);
    @Query
    boolean existsByManagerName(String managerName);

}
