package com.example.qa.im;

import com.example.qa.errorhandling.MessageException;
import com.example.qa.im.exchange.MessagePayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class IMController {

    private final IMOrderUserValidator validator;
    private final IMService imService;

    public IMController(@Autowired IMOrderUserValidator validator, @Autowired IMService imService) {
        this.validator = validator;
        this.imService = imService;
    }

    @MessageMapping("/{orderId}")
    public void sendMessage(@DestinationVariable long orderId, @Payload MessagePayload payload, Principal auth) {
        log.info("send message {} {}", orderId, payload);
        var res = validator.check(orderId, auth);
        if (payload.msgBody() == null) {
            throw new MessageException(HttpStatus.BAD_REQUEST, "No message body");
        }
        imService.sendFromUser(res.order(), res.sender(), payload.msgBody());
    }

    @ResponseBody
    @GetMapping("/im/history/{orderId}")
    public List<MessagePayload> getHistory(@PathVariable long orderId, Principal auth) {
        validator.check(orderId, auth);
        return imService.getOrderHistoryMessages(orderId)
                .stream()
                .map(MessagePayload::new)
                .collect(Collectors.toList());
    }


}
