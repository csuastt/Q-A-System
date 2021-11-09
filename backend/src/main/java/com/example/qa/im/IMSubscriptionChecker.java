package com.example.qa.im;

import com.example.qa.security.UserAuthentication;
import com.example.qa.utils.AbstractSubscriptionChecker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Log4j2
@Configuration
public class IMSubscriptionChecker extends AbstractSubscriptionChecker {

    private final IMOrderUserValidator validator;

    public IMSubscriptionChecker(@Autowired IMOrderUserValidator validator) {
        super("/im/receive/");
        this.validator = validator;
    }

    @Override
    protected void internalCheck(UserAuthentication authUser, String substring, Message<?> message) {
        long orderId = -1;
        try {
            orderId = Long.parseLong(substring);
        } catch (NumberFormatException e) {
            log.warn("IM subscribe failed: Invalid orderId in destination string [{}]", substring);
        }
        var res = validator.check(orderId, authUser);
        log.info("User[id={}] subscribed to Order[id={}] IM successfully", res.sender().getId(), orderId);
    }
}
