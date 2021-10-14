package com.example.qa.order;

import com.example.qa.order.exchange.AcceptData;
import com.example.qa.order.exchange.OrderData;
import com.example.qa.order.exchange.OrderEditData;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.order.repository.OrderRepository;
import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public OrderData create(@RequestBody OrderEditData data) {
        boolean isAdmin = false;
        AppUser[] users = checkOrderData(data, null);
        Order order = new Order(data, users[0], users[1], isAdmin);
        orderRepository.save(order);
        return new OrderData(order);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable(value = "id") long id) {
        Order order = getById(id, true);
        if (order.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public OrderData query(@PathVariable(value = "id") long id) {
        boolean isAdmin = false;
        return new OrderData(getById(id, isAdmin));
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void edit(@PathVariable(value = "id") long id, @RequestBody OrderEditData data) {
        Order order = getById(id, false);
        AppUser[] users = checkOrderData(data, order);
        order.update(data, users[0], users[1]);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public void review(@PathVariable(value = "id") long id, @RequestBody AcceptData data) {
        Order order = getById(id, false);
        if (order.getState() != OrderState.PAYED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_TO_BE_REVIEWED");
        }
        order.setState(data.isAccept() ? OrderState.REVIEWED : OrderState.REJECTED_BY_REVIEWER);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/respond")
    @ResponseStatus(value = HttpStatus.OK)
    public void respond(@PathVariable(value = "id") long id, @RequestBody AcceptData data) {
        Order order = getById(id, false);
        // TODO: 检查是否是回答者
        if (order.getState() != OrderState.REVIEWED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_TO_BE_RESPONDED");
        }
        order.setState(data.isAccept() ? OrderState.ACCEPTED : OrderState.REJECTED_BY_ANSWERER);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/end")
    @ResponseStatus(value = HttpStatus.OK)
    public void endChat(@PathVariable(value = "id") long id) {
        Order order = getById(id, false);
        // TODO: 检查是否是合法的操作者
        if (order.getState() != OrderState.ANSWERED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_TO_BE_ENDED");
        }
        order.setState(OrderState.CHAT_ENDED);
        orderRepository.save(order);
    }

    @GetMapping
    public OrderData[] queryList() {
        // TODO: 完成此接口
        return orderRepository.findAll().stream().map(OrderData::new).toArray(OrderData[]::new);
    }

    private Order getById(long id, boolean allowDeleted) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty() || (order.get().isDeleted() && !allowDeleted)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return order.get();
    }

    private AppUser[] checkOrderData(OrderEditData data, Order original) {
        boolean isCreation = original == null;
        if (isCreation && (data.getAsker() == null || data.getAnswerer() == null || data.getQuestion() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        long newAsker = isCreation || data.getAsker() != null ? data.getAsker() : original.getAsker().getId();
        if (!userRepository.existsByIdAndEnable(newAsker, true)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ASKER_INVALID");
        }
        long newAnswerer = isCreation || data.getAnswerer() != null ? data.getAnswerer() : original.getAnswerer().getId();
        if (newAsker == newAnswerer
                || !userRepository.existsByIdAndEnable(newAnswerer, true)
                || !userRepository.getById(newAnswerer).getPermit().equals("a")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ANSWERER_INVALID");
        }
        String newQuestion = isCreation || data.getQuestion() != null ? data.getQuestion() : original.getQuestion();
        if (newQuestion == null || newQuestion.length() < 5 || newQuestion.length() > 100) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "QUESTION_INVALID");
        }
        return new AppUser[]{userRepository.getById(newAsker), userRepository.getById(newAnswerer)};
    }
}
