package com.example.qa.order;

import com.example.qa.config.SystemConfig;
import com.example.qa.im.IMService;
import com.example.qa.notification.NotificationService;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.UserService;
import com.example.qa.user.model.User;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.example.qa.order.model.OrderState.ANSWERED;
import static com.example.qa.order.model.OrderState.completedOrderStates;

@Log4j2
@Service
public class OrderService {
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final IMService imService;
    @Setter
    private PageRequest pageRequest;

    public OrderService(UserService userService, OrderRepository orderRepository, NotificationService notificationService, @Lazy IMService imService) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.imService = imService;
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
                orderRepository.findAllByDeletedAndStateInAndShowPublic(false, completedOrderStates, true, pageRequest) :
                orderRepository.findAllByDeletedAndStateInAndShowPublicAndQuestionTitleContains(false, completedOrderStates, true, keyword, pageRequest);
    }

    @Scheduled(cron = "*/10 * * * * *")  // every 10 seconds
    public void clearExpirations() {
        List<Order> notifyList = orderRepository.findAllByNotifyTimeBefore(ZonedDateTime.now());
        if (!notifyList.isEmpty()) {
            log.info("Clearing notify orders...");
            notifyList.forEach(this::handleNotify);
            log.info("Finished clearing notify orders");
        }
        List<Order> expireList = orderRepository.findAllByExpireTimeBefore(ZonedDateTime.now());
        if (!expireList.isEmpty()) {
            log.info("Clearing expired orders...");
            expireList.forEach(this::handleExpiration);
            log.info("Finished clearing expired orders");
        }
    }

    public void handleExpiration(Order order) {
        log.info("Order #{} expired", order.getId());
        if (order.getState() == OrderState.REVIEWED) {
            order.setState(OrderState.RESPOND_TIMEOUT);
            userService.refund(order);
            order.setExpireTime(null);
            order = save(order);
            notificationService.send(Notification.ofDeadlineOrTimeout(
                    order.getAnswerer(), order, Notification.Type.ACCEPT_TIMEOUT, order.getExpireTime()
            ));
        } else if (order.getState() == OrderState.ACCEPTED) {
            order.setState(OrderState.ANSWER_TIMEOUT);
            userService.refund(order);
            order.setExpireTime(null);
            order = save(order);
            notificationService.send(Notification.ofDeadlineOrTimeout(
                    order.getAnswerer(), order, Notification.Type.ANSWER_TIMEOUT, order.getExpireTime()
            ));
        } else if (order.getState() == OrderState.ANSWERED) {
            order.setState(OrderState.CHAT_ENDED);
            order.setEndReason(OrderEndReason.TIME_LIMIT);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getFulfillExpirationSeconds()));
            order = save(order);
            imService.sendFromSystem(order, "达到聊天时间上限，系统自动结束聊天");
            notificationService.send(Notification.ofOrderStateChanged(order.getAsker(), order));
            notificationService.send(Notification.ofOrderStateChanged(order.getAnswerer(), order));
        } else if (order.getState() == OrderState.CHAT_ENDED) {
            int fee = order.getPrice() * SystemConfig.getFeeRate() / 100;
            userService.addEarnings(order.getAnswerer(), order.getPrice() - fee);
            SystemConfig.incEarnings(fee);
            order.setState(OrderState.FULFILLED);
            order.setExpireTime(null);
            order = save(order);
            notificationService.send(Notification.ofOrderStateChanged(order.getAnswerer(), order));
        } else {
            order.setExpireTime(null);
            save(order);
        }
    }

    public void handleNotify(Order order) {
        order.setNotifyTime(null);
        order = save(order);
        if (order.getState() == OrderState.REVIEWED) {
            notificationService.send(Notification.ofDeadlineOrTimeout(
                    order.getAnswerer(), order, Notification.Type.ACCEPT_DEADLINE, order.getExpireTime()
            ));
        } else if (order.getState() == OrderState.ACCEPTED) {
            notificationService.send(Notification.ofDeadlineOrTimeout(
                    order.getAnswerer(), order, Notification.Type.ANSWER_DEADLINE, order.getExpireTime()
            ));
        }
        log.info("Sent notification for order #{}", order.getId());
    }

    public Order newMessage(Order order) {
        order.setMessageCount(order.getMessageCount() + 1);
        order = save(order);
        if (order.getState() == ANSWERED && order.getMessageCount() >= SystemConfig.getMaxChatMessages()) {
            order.setState(OrderState.CHAT_ENDED);
            order.setEndReason(OrderEndReason.MESSAGE_LIMIT);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getFulfillExpirationSeconds()));
            order = save(order);
            imService.sendFromSystem(order, "聊天消息数量达到上限，系统自动结束聊天");
            notificationService.send(Notification.ofOrderStateChanged(order.getAsker(), order));
            notificationService.send(Notification.ofOrderStateChanged(order.getAnswerer(), order));
        }
        return order;
    }
}
