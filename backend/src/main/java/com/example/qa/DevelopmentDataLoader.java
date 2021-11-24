package com.example.qa;

import com.example.qa.order.OrderRepository;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.model.Order;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Profile({"dev", "!test"})
public class DevelopmentDataLoader implements ApplicationRunner {
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final PasswordEncoder encoder;
    private static final Random random = new Random();

    @Autowired
    public DevelopmentDataLoader(UserRepository userRepo, OrderRepository orderRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.encoder = encoder;
    }

    private static final String ANSWER = "我可以回答你一句“无可奉告”。";

    private Order randomNewOrder(User asker, User answerer) {
        var data = new OrderRequest();
        var question = "为什么随机字符串是 " + RandomStringUtils.random(5, true, false) + "？";
        data.setTitle(question);
        data.setDescription("顺便问一下为什么 $ \\int_0^1 x dx = \\frac 1 2 $？");
        data.setState(Order.State.values()[random.nextInt(Order.State.values().length)]);
        if (data.getState() == Order.State.CREATED && random.nextInt(100) >= 90) {
            data.setState(Order.State.REVIEWED);
        }
        if (data.getState() == Order.State.ANSWERED) {
            data.setAnswer(ANSWER);
        } else if (data.getState() == Order.State.CHAT_ENDED || data.getState() == Order.State.FULFILLED) {
            data.setAnswer(ANSWER);
            data.setEndReason(Order.EndReason.values()[random.nextInt(Order.EndReason.values().length)]);
        }
        var order = new Order(data, asker, answerer, true);
        if (order.getState() == Order.State.REVIEWED || order.getState() == Order.State.ACCEPTED || order.getState() == Order.State.ANSWERED || order.getState() == Order.State.CHAT_ENDED) {
            order.setExpireTime(ZonedDateTime.now().plusWeeks(1));
        }
        if (Order.State.completedOrderStates.contains(order.getState())) {
            order.setRating(random.nextInt(6));
        }
        if (order.getRating() > 0) {
            order.setRatingText("好！");
        }
        order.setShowPublic(random.nextBoolean());
        if (order.isShowPublic()) {
            order.setPublicPrice(random.nextBoolean() ? 0 : random.nextInt(order.getPrice()));
        }
        return order;
    }

    private User randomNewUser(String username) {
        var rr = new RegisterRequest();
        rr.setUsername(username);
        rr.setPassword(encoder.encode(username));
        rr.setEmail("test@example.com");
        var user = new User(rr);
        user.setAskCount(random.nextInt(20));
        return user;
    }

    private User makeAnswerer(User user) {
        user.setRole(User.Role.ANSWERER);
        user.setPrice(random.nextInt(100) + 1);
        user.setDescription("我是" + user.getNickname() + "EwbkK8TU" + "领域" + random.nextInt(10));
        user.setAnswerCount(random.nextInt(20));
        AtomicInteger total = new AtomicInteger(0);
        var earningsList = IntStream.range(0, 3)
                .mapToObj(i -> {
                    int earnings = random.nextInt(100) + 1;
                    total.addAndGet(earnings);
                    return String.format("{\"date\":\"2021-%02d-01\",\"earnings\":%d}", random.nextInt(3) + 3 * i + 1, earnings);
                }).collect(Collectors.toList());
        user.setEarningsTotal(total.get());
        user.setEarningsMonthly("[" + String.join(",", earningsList) + "]");
        IntStream.range(0, random.nextInt(20)).forEach(i -> user.addRating(random.nextInt(5) + 1));
        return user;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Test user data. 50 normal users & 50 answerers
        var userList =
                IntStream.range(0, 20)
                        .mapToObj(i -> randomNewUser("testuser-" + i))
                        .collect(Collectors.toList());
        userList = userRepo.saveAll(userList);
        var answererList =
                IntStream.range(0, 20)
                        .mapToObj(i -> randomNewUser("answerer-" + i))
                        .map(this::makeAnswerer)
                        .collect(Collectors.toList());
        answererList = userRepo.saveAll(answererList);

        // Order. 50 * 50 = 2500 orders.
        var finalAnswererList = answererList;
        userList.stream()
                .flatMap(user ->
                        finalAnswererList.stream()
                                .map(answerer -> randomNewOrder(user, answerer))
                )
                .forEach(orderRepo::save);
    }
}
