package com.example.qa.notification;

import com.example.qa.notification.exchange.NotifPayload;
import com.example.qa.notification.exchange.PagedList;
import com.example.qa.notification.model.Notification;
import com.example.qa.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final SimpMessagingTemplate template;

    public NotificationService(@Autowired NotificationRepository repo, @Autowired SimpMessagingTemplate template) {
        this.repo = repo;
        this.template = template;
    }

    public PagedList<Notification> getNotifications(User user, Optional<Boolean> hasRead, Pageable pageable) {
        return new PagedList<>(hasRead
                .map(hasReadValue -> repo.findByReceiverAndHaveReadOrderByCreateTimeDesc(user, hasReadValue, pageable))
                .orElseGet(() -> repo.findByReceiverOrderByCreateTimeDesc(user, pageable)));
    }

    public void send(Notification notif) {
        repo.saveAndFlush(notif);
        template.convertAndSend("/notif/" + notif.getReceiver().getId(), new NotifPayload(notif));
    }

    public void readAll(User user) {
        var notifList = repo.findByReceiverAndHaveReadOrderByCreateTimeDesc(user, true, Pageable.unpaged());
        notifList.forEach(notif -> notif.setHaveRead(true));
        repo.saveAll(notifList);
    }

    public void readOne(Notification notif) {
        notif.setHaveRead(true);
        repo.saveAndFlush(notif);
    }

    public void deleteRead(User user) {
        repo.deleteByReceiverAndHaveReadIsTrue(user);
    }
}
