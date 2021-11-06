package com.example.qa.im;

import com.example.qa.im.exchange.MessagePayload;
import com.example.qa.im.model.Message;
import com.example.qa.notification.NotificationService;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.model.Order;
import com.example.qa.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class IMService {

    private final MessageRepository repo;
    private final NotificationService notifService;
    private final SimpMessagingTemplate template;

    public IMService(
            @Autowired MessageRepository repo,
            @Autowired NotificationService notifService,
            @Autowired SimpMessagingTemplate template) {
        this.repo = repo;
        this.notifService = notifService;
        this.template = template;
    }

    public final void sendFromUser(Order order, User sender, String body) {
        var msg = Message.builder().order(order).sender(sender).body(body).build();
        processNewMessage(msg);
    }

    public final void sendFromSystem(Order order, String body) {
        var msg = Message.builder().order(order).body(body).build();
        processNewMessage(msg);
    }

    public final List<Message> getOrderHistoryMessages(long orderId) {
        return repo.findAllByOrder_IdOrderBySendTimeAsc(orderId);
    }

    public final List<Message> getOrderHistoryMessages(Order order) {
        return repo.findAllByOrder_IdOrderBySendTimeAsc(order.getId());
    }

    private void processNewMessage(Message msg) {
        repo.saveAndFlush(msg);
        var notif =
                Notification.builder()
                        .type(Notification.Type.NEW_MESSAGE)
                        .haveRead(false)
                        .createTime(ZonedDateTime.now())
                        .receiver(msg.getSender())
                        .target(msg.getOrder())
                        .msgSummary(msg.getBody())
                        .build();
        notifService.send(notif);
        template.convertAndSend("/im/receive/" + msg.getOrder().getId(), new MessagePayload(msg));
    }
}
