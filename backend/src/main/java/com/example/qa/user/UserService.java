package com.example.qa.user;

import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsById(long id) {
        return userRepository.existsByIdAndDeleted(id, false);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getById(long id) {
        return getById(id, false);
    }

    public User getById(long id, boolean allowDeleted) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        User user = userOptional.get();
        if (user.isDeleted() && !allowDeleted) {
            throw new UsernameNotFoundException(null);
        }
        return user;
    }

    public User getByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        return userOptional.get();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Page<User> listByRole(UserRole role, Pageable pageable) {
        return role == null ? userRepository.findAll(pageable) : userRepository.findAllByRole(role, pageable);
    }
}
