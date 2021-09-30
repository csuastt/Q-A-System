package com.example.qa.manager.repository;

import com.example.qa.manager.model.AppManager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ManagerRepository extends CrudRepository<AppManager, String> {
    @Query
    List<AppManager> findAllByPermission(String permission);
}
