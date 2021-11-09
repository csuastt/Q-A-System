package com.example.qa.utils;

import com.example.qa.errorhandling.MessageException;
import com.example.qa.security.UserAuthentication;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;

@Log4j2
public abstract class AbstractSubscriptionChecker implements ChannelInterceptor, WebSocketMessageBrokerConfigurer {
    private final String subscriptionHeader;

    protected AbstractSubscriptionChecker(String subscriptionHeader) {
        this.subscriptionHeader = subscriptionHeader;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(this);
    }

    @Nullable
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//            try {
                var auth = accessor.getUser();
                if (auth instanceof UserAuthentication authUser) {
                    var dest = Objects.requireNonNullElse(accessor.getDestination(), "");
                    if (dest.startsWith(subscriptionHeader)) {
                        internalCheck(authUser, dest.substring(subscriptionHeader.length()), message);
                    }
                } else {
                    throw new MessageException(HttpStatus.UNAUTHORIZED);
                }
//            } catch (MessageException e) {
//                log.warn(e);
//                var msgHeader = StompHeaderAccessor.create(StompCommand.ERROR);
//                msgHeader.setMessage(e.getMessage());
//                msgHeader.setSessionId(accessor.getSessionId());
//                channel.send(MessageBuilder.createMessage(new byte[0], msgHeader.getMessageHeaders()));
//            }
        }
        return message;
    }

    protected abstract void internalCheck(UserAuthentication authUser, String substring, Message<?> message);
}
