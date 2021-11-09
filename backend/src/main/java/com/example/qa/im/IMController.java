package com.example.qa.im;

import com.example.qa.errorhandling.MessageException;
import com.example.qa.im.exchange.MessagePayload;
import com.example.qa.order.OrderRepository;
import com.example.qa.order.model.Order;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.UserRepository;
import com.example.qa.user.model.User;
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

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final IMService imService;

    public IMController(
            @Autowired OrderRepository orderRepo,
            @Autowired UserRepository userRepo,
            @Autowired IMService imService) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.imService = imService;
    }

    @MessageMapping("/{orderId}")
    public void sendMessage(@DestinationVariable long orderId, @Payload MessagePayload payload, Principal user) {
        log.info("send message {} {}", orderId, payload);
        var res = checkOrderAndUser(orderId, user);
        if (payload.getMsgBody() == null) {
            throw new MessageException(HttpStatus.BAD_REQUEST, "No message body");
        }
        imService.sendFromUser(res.order, res.sender, payload.getMsgBody());
    }

    @ResponseBody
    @GetMapping("/im/history/{orderId}")
    public List<MessagePayload> getHistory(@PathVariable long orderId, Principal user) {
        checkOrderAndUser(orderId, user);
        return imService.getOrderHistoryMessages(orderId)
                .stream()
                .map(MessagePayload::new)
                .collect(Collectors.toList());
    }

    private CheckResult checkOrderAndUser(long orderId, Principal user) {
        var order = orderRepo.findById(orderId).orElseThrow(() -> new MessageException(HttpStatus.NOT_FOUND, "Order"));
        if (user instanceof UserAuthentication authUser) {
            long userId = (long) authUser.getPrincipal();
            var optionalUser = userRepo.findById(userId).orElseThrow(() -> new MessageException(HttpStatus.NOT_FOUND, "User"));
            if (userId != order.getAsker().getId() && userId != order.getAnswerer().getId()) {
                throw new MessageException(HttpStatus.FORBIDDEN, "Current user is not related to requested order");
            }
            return new CheckResult(order, optionalUser);
        } else {
            throw new MessageException(HttpStatus.UNAUTHORIZED);
        }
    }

    public record CheckResult(Order order, User sender) {
    }
}
