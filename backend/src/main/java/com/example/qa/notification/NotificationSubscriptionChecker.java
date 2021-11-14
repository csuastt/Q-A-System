package com.example.qa.notification;

import com.example.qa.errorhandling.MessageException;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.UserRepository;
import com.example.qa.utils.AbstractSubscriptionChecker;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;

@Log4j2
@Configuration
public class NotificationSubscriptionChecker extends AbstractSubscriptionChecker {

    private final UserRepository userRepo;

    public NotificationSubscriptionChecker(UserRepository userRepo) {
        super("/notif/");
        this.userRepo = userRepo;
    }

    @Override
    protected void internalCheck(UserAuthentication authUser, String substring, Message<?> message) {
        long userId = -1;
        try {
            userId = Long.parseLong(substring);
        } catch (NumberFormatException e) {
            log.warn("Notification subscribe failed: Invalid userId in destination string [{}]", substring);
        }

        long authUserId = (long) authUser.getPrincipal();
        if (authUserId != userId) {
            throw new MessageException(HttpStatus.FORBIDDEN, "Cannot subscribe to other user's notifications");
        }
        if (userRepo.findById(userId).isEmpty()) {
            throw new MessageException(HttpStatus.NOT_FOUND, "User");
        }

        log.info("User[id={}] subscribed to his notification list successfully", userId);
    }

}
