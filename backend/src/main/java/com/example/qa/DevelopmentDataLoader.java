package com.example.qa;


import com.example.qa.order.OrderRepository;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Profile({"dev", "!test"})
public class DevelopmentDataLoader implements ApplicationRunner {
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public DevelopmentDataLoader(UserRepository userRepo, OrderRepository orderRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.encoder = encoder;
    }

    private static OrderRequest genOrderData() {
        var data = new OrderRequest();
        data.setQuestion("Test Question [" + RandomStringUtils.random(5, true, false) + "]");
        data.setPrice(100);
        data.setState(OrderState.CREATED);
        return data;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Test user data. 50 normal users & 50 answerers
        var userList =
                IntStream.range(0, 50)
                        .mapToObj(i -> "testuser-" + i)
                        .map(name -> {
                            var rr = new RegisterRequest();
                            rr.setUsername(name);
                            rr.setPassword(encoder.encode(name));
                            rr.setEmail("test@example.com");
                            return rr;
                        })
                        .map(User::new)
                        .collect(Collectors.toList());
        userRepo.saveAll(userList);
        var answererList =
                IntStream.range(0, 10)
                        .mapToObj(i -> "answerer-" + i)
                        .map(name -> {
                            var rr = new RegisterRequest();
                            rr.setUsername(name);
                            rr.setPassword(encoder.encode(name));
                            rr.setEmail("test@example.com");
                            return rr;
                        })
                        .map(User::new)
                        .peek(user -> user.setRole(UserRole.ANSWERER))
                        .collect(Collectors.toList());
        userRepo.saveAll(answererList);

        // Order. 50 * 50 = 2500 orders.
        userList.stream()
                .flatMap(user ->
                        answererList.stream()
                                .map(answerer -> new Order(genOrderData(), user, answerer, true))
                )
                .forEach(orderRepo::save);
    }
}
