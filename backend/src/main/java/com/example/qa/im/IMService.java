package com.example.qa.im;

import com.example.qa.im.exchange.MessagePayload;
import com.example.qa.im.model.Message;
import com.example.qa.notification.NotificationService;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.OrderService;
import com.example.qa.order.model.Order;
import com.example.qa.user.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Log4j2
@Service
public class IMService {

    private final MessageRepository repo;
    private final NotificationService notifService;
    private final OrderService orderService;
    private final SimpMessagingTemplate template;

    public IMService(
            @Autowired MessageRepository repo,
            @Autowired NotificationService notifService,
            @Autowired OrderService orderService,
            @Autowired SimpMessagingTemplate template) {
        this.repo = repo;
        this.notifService = notifService;
        this.orderService = orderService;
        this.template = template;
    }

    public final void sendFromUser(Order order, User sender, ZonedDateTime sendTime, String body) {
        if (order.getState() != Order.State.ANSWERED) {
            log.warn("Discard IMMessage: Order State {} is not ANSWERED.", order.getState());
            return;
        }
        processNewMessage(Message.builder()
                .order(order)
                .sender(sender)
                .sendTime(sendTime)
                .body(body)
                .build());
        orderService.newMessage(order);
    }

    public final void sendFromSystem(Order order, String body) {
        processNewMessage(Message.builder()
                .order(order)
                .sendTime(ZonedDateTime.now())
                .body(body)
                .build());
    }

    public final List<Message> getOrderHistoryMessages(long orderId) {
        return repo.findAllByOrder_IdOrderBySendTimeAsc(orderId);
    }

    public final List<Message> getOrderHistoryMessages(Order order) {
        return repo.findAllByOrder_IdOrderBySendTimeAsc(order.getId());
    }

    private void processNewMessage(Message msg) {
        repo.saveAndFlush(msg);
        var notif = Notification.ofNewMessage(msg.getOrder().getAnswerer(), msg.getOrder(), msg.getBody());
        notifService.send(notif);
        notif.setReceiver(msg.getOrder().getAsker());
        notifService.send(notif);
        template.convertAndSend("/im/receive/" + msg.getOrder().getId(), new MessagePayload(msg));
    }
}
