package com.example.qa.notification;

import com.example.qa.notification.model.Notification;
import com.example.qa.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByReceiverAndHaveReadIsTrue(User receiver);

    Page<Notification> findByReceiverOrderByCreateTimeDesc(User receiver, Pageable pageable);

    Page<Notification> findByReceiverAndHaveReadOrderByCreateTimeDesc(User receiver, boolean haveRead, Pageable pageable);

}
