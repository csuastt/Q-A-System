package com.example.qa.manager;

import com.example.qa.manager.model.AppManager;
import com.example.qa.manager.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class DataLoader implements ApplicationRunner {

    private ManagerRepository managerRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(ManagerRepository managerRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(ApplicationArguments args) {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("manager"));
        var manager = new AppManager("testManager", passwordEncoder.encode("password"), authorities);
        managerRepository.save(manager);
    }
}
