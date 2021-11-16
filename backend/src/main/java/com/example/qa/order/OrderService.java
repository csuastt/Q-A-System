package com.example.qa.order;

import com.example.qa.config.SystemConfig;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.UserService;
import com.example.qa.user.model.User;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.example.qa.order.model.OrderState.publicOrderStates;

@Service
public class OrderService {
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Setter
    private PageRequest pageRequest;

    public OrderService(UserService userService, OrderRepository orderRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public Page<Order> listByAsker(User asker, Boolean finished) {
        return finished == null ?
                orderRepository.findAllByDeletedAndAsker(false, asker, pageRequest) :
                orderRepository.findAllByDeletedAndAskerAndFinished(false, asker, finished, pageRequest);
    }

    public Page<Order> listByAnswerer(User answerer, Boolean finished) {
        return finished == null ?
                orderRepository.findAllByDeletedAndVisibleToAnswererAndAnswerer(false, true, answerer, pageRequest) :
                orderRepository.findAllByDeletedAndVisibleToAnswererAndAnswererAndFinished(false, true, answerer, finished, pageRequest);
    }

    public Page<Order> listByState(Collection<OrderState> state) {
        return state == null ?
                orderRepository.findAll(pageRequest) :
                orderRepository.findAllByDeletedAndStateIn(false, state, pageRequest);
    }

    public Page<Order> listByReviewed() {
        return orderRepository.findAllByDeletedAndReviewed(false, true, pageRequest);
    }

    public Page<Order> listByPublic(String keyword) {
        return keyword == null ?
                orderRepository.findAllByDeletedAndStateInAndShowPublic(false, publicOrderStates, true, pageRequest) :
                orderRepository.findAllByDeletedAndStateInAndShowPublicAndQuestionTitleContains(false, publicOrderStates, true, keyword, pageRequest);
    }

    @Scheduled(cron = "*/10 * * * * *")  // every 10 seconds
    public void clearExpirations() {
        List<Order> orderList = orderRepository.findAllByExpireTimeBefore(ZonedDateTime.now());
        if (orderList.isEmpty()) {
            return;
        }
        log.info("Clearing expired orders...");
        orderList.forEach(this::handleExpiration);
        log.info("Finished clearing expired orders");
    }

    public void handleExpiration(Order order) {
        log.info("Order #{} expired", order.getId());
        if (order.getState() == OrderState.REVIEWED) {
            order.setState(OrderState.RESPOND_TIMEOUT);
            userService.refund(order);
            order.setExpireTime(null);
        } else if (order.getState() == OrderState.ACCEPTED) {
            order.setState(OrderState.ANSWER_TIMEOUT);
            userService.refund(order);
            order.setExpireTime(null);
        } else if (order.getState() == OrderState.ANSWERED) {
            order.setState(OrderState.CHAT_ENDED);
            order.setEndReason(OrderEndReason.TIME_LIMIT);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getFulfillExpirationSeconds()));
        } else if (order.getState() == OrderState.CHAT_ENDED) {
            int fee = order.getPrice() * SystemConfig.getFeeRate() / 100;
            userService.addEarnings(order.getAnswerer(), order.getPrice() - fee);
            SystemConfig.incEarnings(fee);
            order.setState(OrderState.FULFILLED);
            order.setExpireTime(null);
        } else {
            order.setExpireTime(null);
        }
        save(order);
    }
}
