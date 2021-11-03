package com.example.qa.user;

import com.example.qa.order.model.Order;
import com.example.qa.user.exchange.MonthlyEarnings;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JsonMapper mapper;

    public UserService(UserRepository userRepository, JsonMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
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

    public User save(User user) {
        return userRepository.save(user);
    }

    public Page<User> listByRole(UserRole role, Pageable pageable) {
        return role == null ? userRepository.findAll(pageable) : userRepository.findAllByRole(role, pageable);
    }

    public User refund(User user, int value) {
        user.setBalance(user.getBalance() + value);
        return save(user);
    }

    public User refund(Order order) {
        return refund(order.getAsker(), order.getPrice());
    }

    public List<MonthlyEarnings> getMonthlyEarningsList(String monthlyEarnings) {
        try {
            return mapper.readerForListOf(MonthlyEarnings.class).readValue(monthlyEarnings);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public User addEarnings(User user, int earnings) {
        List<MonthlyEarnings> monthlyEarningsList = getMonthlyEarningsList(user.getEarningsMonthly());
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        if (monthlyEarningsList.isEmpty() || !monthlyEarningsList.get(monthlyEarningsList.size() - 1).getDate().isEqual(thisMonth)) {
            monthlyEarningsList.add(new MonthlyEarnings(thisMonth, earnings));
        } else {
            MonthlyEarnings monthlyEarnings = monthlyEarningsList.get(monthlyEarningsList.size() - 1);
            monthlyEarnings.setEarnings(monthlyEarnings.getEarnings() + earnings);
        }
        try {
            user.setEarningsMonthly(mapper.writeValueAsString(monthlyEarningsList));
        } catch (JsonProcessingException e) {
            user.setEarningsMonthly("[]");
        }
        user.setEarningsTotal(user.getEarningsTotal() + earnings);
        return save(user);
    }
}
