package com.example.qa.order;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.order.exchange.AcceptRequest;
import com.example.qa.order.exchange.OrderListResponse;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.UserService;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.FieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.qa.security.RestControllerAuthUtils.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderRepository orderRepository;

    public OrderController(UserService userService, OrderRepository orderRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest data) {
        if (!authLogin()) {
            throw new ApiException(401);
        }
        if (authIsUser()) {
            data.setAsker(authGetUserId());
        }
        User[] users = checkOrderData(data);
        Order order = new Order(data, users[0], users[1], authIsAdmin());
        orderRepository.save(order);
        return new OrderResponse(order, authIsAdmin() ? 2 : 1);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteOrder(@PathVariable(value = "id") long id) {
        Order order = getById(id, true);
        if (order.isDeleted()) {
            throw new ApiException(HttpStatus.FORBIDDEN);
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public OrderResponse queryOrder(@PathVariable(value = "id") long id) {
        return new OrderResponse(getById(id, authIsAdmin()));
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void editOrder(@PathVariable(value = "id") long id, @RequestBody OrderRequest data) {
        Order order = getById(id, false);
        order.update(data);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/pay")
    @ResponseStatus(value = HttpStatus.OK)
    public void payOrder(@PathVariable(value = "id") long id) {
        if (!authLogin()) {
            throw new ApiException(401);
        }
        Order order = getById(id, false);
        User asker = order.getAsker();
        if (!authIsUser(asker.getId())) {
            throw new ApiException(403, "NO_PERMISSION");
        }
        if (order.getState() != OrderState.CREATED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_REVIEWED");
        }
        if (asker.getBalance() < order.getPrice()) {
            throw new ApiException(403, "BALANCE_NOT_ENOUGH");
        }
        asker.setBalance(asker.getBalance() - order.getPrice());
        userService.save(asker);
        order.setState(OrderState.PAYED);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/review")
    @ResponseStatus(value = HttpStatus.OK)
    public void reviewOrder(@PathVariable(value = "id") long id, @RequestBody AcceptRequest data) {
        Order order = getById(id, false);
        if (order.getState() != OrderState.PAYED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_REVIEWED");
        }
        order.setState(data.isAccept() ? OrderState.REVIEWED : OrderState.REJECTED_BY_REVIEWER);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/respond")
    @ResponseStatus(value = HttpStatus.OK)
    public void respondOrder(@PathVariable(value = "id") long id, @RequestBody AcceptRequest data) {
        Order order = getById(id, false);
        if (order.getState() != OrderState.REVIEWED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_RESPONDED");
        }
        order.setState(data.isAccept() ? OrderState.ACCEPTED : OrderState.REJECTED_BY_ANSWERER);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/end")
    @ResponseStatus(value = HttpStatus.OK)
    public void endChat(@PathVariable(value = "id") long id) {
        Order order = getById(id, false);
        if (order.getState() != OrderState.ANSWERED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_ENDED");
        }
        order.setState(OrderState.CHAT_ENDED);
        orderRepository.save(order);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public void cancelOrder(@PathVariable(value = "id") long id) {
        if (!authLogin()) {
            throw new ApiException(401);
        }
        Order order = getById(id, false);
        User asker = order.getAsker();
        if (!authIsUser(asker.getId())) {
            throw new ApiException(403, "NO_PERMISSION");
        }
        if (order.getState() != OrderState.CREATED
                && order.getState() != OrderState.PAYED
                && order.getState() != OrderState.REVIEWED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "CANNOT_CANCEL");
        }
        order.setState(OrderState.CANCELLED);
        orderRepository.save(order);
    }

    @GetMapping
    public OrderListResponse queryList(
            @RequestParam(required = false) Long asker,
            @RequestParam(required = false) Long answerer,
            @RequestParam(required = false) OrderState state,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        if (!authLogin()) {
            throw new ApiException(401);
        }
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.ORDER_LIST_MAX_PAGE_SIZE);
        PageRequest pageRequest = PageRequest.ofSize(pageSize).withPage(page - 1);
        Page<Order> result;
        if (authIsAdmin()) {
            if (state != OrderState.PAYED) {
                throw new ApiException(403);
            }
            result = orderRepository.findAllByStateAndDeleted(OrderState.PAYED, false, pageRequest);
        } else {
            pageRequest = pageRequest.withSort(Sort.by(Sort.Direction.DESC, "createTime"));
            if (asker != null && authIsUser(asker)) {
                result = orderRepository.findAllByAskerAndDeleted(userService.getById(asker), false, pageRequest);
            } else if (answerer != null && authIsUser(answerer)) {
                result = orderRepository.findAllByAnswererAndDeletedAndReviewed(
                        userService.getById(answerer), false, true, pageRequest
                );
            } else {
                throw new ApiException(403);
            }
        }
        return new OrderListResponse(result, authIsAdmin() ? 2 : 0);
    }

    private Order getById(long id, boolean allowDeleted) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty() || (order.get().isDeleted() && !allowDeleted)) {
            throw new ApiException(HttpStatus.NOT_FOUND);
        }
        return order.get();
    }

    private User[] checkOrderData(OrderRequest data) {
        if (data.getAsker() == null || data.getAnswerer() == null || data.getQuestion() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST);
        }
        long newAsker = data.getAsker();
        if (!userService.existsById(newAsker)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ASKER_INVALID");
        }
        long newAnswerer = data.getAnswerer();
        if (newAsker == newAnswerer
                || !userService.existsById(newAnswerer)
                || userService.getById(newAnswerer).getRole() != UserRole.ANSWERER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ANSWERER_INVALID");
        }
        if (!FieldValidator.length
                (data.getQuestion(), SystemConfig.QUESTION_MIN_LENGTH, SystemConfig.QUESTION_MAX_LENGTH)
        ) {
            throw new ApiException(HttpStatus.FORBIDDEN, "QUESTION_INVALID");
        }
        return new User[]{userService.getById(newAsker), userService.getById(newAnswerer)};
    }
}
