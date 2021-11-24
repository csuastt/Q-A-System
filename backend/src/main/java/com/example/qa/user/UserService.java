package com.example.qa.user;

import com.example.qa.exchange.MonthlyEarnings;
import com.example.qa.order.model.Order;
import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        return userOptional.get();
    }

    public User getByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        return userOptional.get();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Page<User> listByRole(Collection<User.Role> role, Pageable pageable) {
        return role == null ? userRepository.findAll(pageable) : userRepository.findAllByRoleIn(role, pageable);
    }

    public Page<User> listByApplying(PageRequest pageRequest) {
        return userRepository.findAllByApplying(true, pageRequest);
    }

    public User refund(User user, int value) {
        user.setBalance(user.getBalance() + value);
        return save(user);
    }

    public User refund(Order order) {
        return refund(order.getAsker(), order.getPrice());
    }

    public User addEarnings(User user, int earnings) {
        user.setEarningsMonthly(MonthlyEarnings.addEarnings(user.getEarningsMonthly(), earnings));
        user.setEarningsTotal(user.getEarningsTotal() + earnings);
        user.setBalance(user.getBalance() + earnings);
        return save(user);
    }
}
