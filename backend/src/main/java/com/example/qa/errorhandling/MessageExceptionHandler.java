package com.example.qa.errorhandling;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ID_HEADER;

@Log4j2
@ControllerAdvice
public class MessageExceptionHandler {

    private final AbstractSubscribableChannel clientOutboundChannel;

    public MessageExceptionHandler(@Autowired AbstractSubscribableChannel clientOutboundChannel) {
        this.clientOutboundChannel = clientOutboundChannel;
    }

    @org.springframework.messaging.handler.annotation.MessageExceptionHandler(MessageException.class)
    public void handlerException(MessageException e, @Header(SESSION_ID_HEADER) String sessionId) {
        log.error(e.getMessage());
        var msgHeader = StompHeaderAccessor.create(StompCommand.ERROR);
        msgHeader.setMessage(e.getMessage());
        msgHeader.setSessionId(sessionId);
        clientOutboundChannel.send(MessageBuilder.createMessage(new byte[0], msgHeader.getMessageHeaders()));
    }

}
