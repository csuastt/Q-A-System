package com.example.qa.manager.service;


import com.example.qa.manager.model.AppManager;
import com.example.qa.manager.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerService implements UserDetailsService {
    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String managername) throws UsernameNotFoundException {
        Optional<AppManager> manager = managerRepository.findById(managername);

        if (manager.isPresent())
            return manager.get();
        else
            throw new UsernameNotFoundException(String.format("Managername[%s] not found", managername));
    }
}
