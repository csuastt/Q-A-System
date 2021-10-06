package com.example.qa.user;

import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(ApplicationArguments args) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new AppUser("testUser", passwordEncoder.encode("password"),authorities);
        var user0 = new AppUser("testUser0", passwordEncoder.encode("password"), authorities);
        var user1 = new AppUser("testUser1", passwordEncoder.encode("password"), authorities);
        user1.setPermit("a");
        var user2 = new AppUser("testUser2", passwordEncoder.encode("password"), authorities);
        var user3 = new AppUser("testUser3", passwordEncoder.encode("password"), authorities);
        var user4 = new AppUser("testUser4", passwordEncoder.encode("password"), authorities);
        var user5 = new AppUser("testUser5", passwordEncoder.encode("password"), authorities);
        var user6 = new AppUser("testUser6", passwordEncoder.encode("password"), authorities);
        userRepository.save(user);
        userRepository.save(user0);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
    }
}