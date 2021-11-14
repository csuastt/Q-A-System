package com.example.qa.im;

import com.example.qa.errorhandling.MessageException;
import com.example.qa.order.OrderRepository;
import com.example.qa.order.model.Order;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.UserRepository;
import com.example.qa.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.function.BiFunction;

@Component
public class IMOrderUserValidator {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    public IMOrderUserValidator(@Autowired OrderRepository orderRepo, @Autowired UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    public Result check(long orderId, Principal auth) {
        return check(orderId, auth, MessageException::new);
    }

    public Result check(long orderId, Principal auth, final BiFunction<HttpStatus, String, ? extends RuntimeException> exceptionConstructor) {
        var order = orderRepo.findById(orderId).orElseThrow(() -> exceptionConstructor.apply(HttpStatus.NOT_FOUND, "Order"));
        if (auth instanceof UserAuthentication authUser) {
            long userId = (long) authUser.getPrincipal();
            var user = userRepo.findById(userId).orElseThrow(() -> exceptionConstructor.apply(HttpStatus.NOT_FOUND, "User"));
            if (userId != order.getAsker().getId() && userId != order.getAnswerer().getId()) {
                throw exceptionConstructor.apply(HttpStatus.FORBIDDEN, "Current user is not related to requested order");
            }
            return new Result(order, user);
        } else {
            throw exceptionConstructor.apply(HttpStatus.UNAUTHORIZED, null);
        }
    }

    public static record Result(Order order, User sender) {
    }

}
