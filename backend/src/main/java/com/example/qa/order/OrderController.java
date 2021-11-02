package com.example.qa.order;

import com.example.qa.admin.AdminService;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.order.exchange.*;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderEndReason;
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

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.example.qa.security.RestControllerAuthUtils.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final AdminService adminService;

    public OrderController(UserService userService, OrderService orderService, AdminService adminService) {
        this.userService = userService;
        this.orderService = orderService;
        this.adminService = adminService;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest data) {
        authLoginOrThrow();
        boolean isAdmin = authIsAdmin();
        if (isAdmin && !authIsSuperAdmin()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        if (!isAdmin) {
            data.setAsker(authGetId());
        }
        User[] users = checkOrderDataOrThrow(data);
        User asker = users[0];
        User answerer = users[1];
        int price = isAdmin ? Objects.requireNonNullElse(data.getPrice(), asker.getPrice()) : asker.getPrice();
        if (asker.getBalance() < price) {
            throw new ApiException(403, "BALANCE_NOT_ENOUGH");
        }
        asker.setBalance(asker.getBalance() - answerer.getPrice());
        asker = userService.save(asker);
        Order order = new Order(data, asker, answerer, isAdmin);
        order = orderService.save(order);
        return new OrderResponse(order, isAdmin ? 2 : 1);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        Order order = getByIdOrThrow(id, true);
        if (order.isDeleted()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ALREADY_DELETED");
        }
        order.setDeleted(true);
        order.setExpireTime(null);
        orderService.save(order);
    }

    @GetMapping("/{id}")
    public OrderResponse queryOrder(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        boolean isAdmin = authIsAdmin();
        long userId = authGetId();
        Order order = getByIdOrThrow(id, isAdmin);
        if (!isAdmin && order.getAsker().getId() != userId && order.getAnswerer().getId() != userId) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        return new OrderResponse(getByIdOrThrow(id, isAdmin), isAdmin ? 2 : 1);
    }

    @PutMapping("/{id}")
    public void editOrder(@PathVariable(value = "id") long id, @RequestBody OrderRequest data) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        Order order = getByIdOrThrow(id, false);
        order.update(data);
        orderService.save(order);
    }

    @PostMapping("/{id}/review")
    public void reviewOrder(@PathVariable(value = "id") long id, @RequestBody AcceptRequest data) {
        authLoginOrThrow();
        if (!authIsAdmin() || adminService.getById(authGetId(), false).getRole() == AdminRole.ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        Order order = getByIdOrThrow(id, false);
        if (order.getState() != OrderState.CREATED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_REVIEWED");
        }
        if (data.isAccept()) {
            order.setState(OrderState.REVIEWED);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getRespondExpirationSeconds()));
        } else {
            order.setState(OrderState.REJECTED_BY_REVIEWER);
            userService.refund(order);
        }
        orderService.save(order);
    }

    @PostMapping("/{id}/respond")
    public void respondOrder(@PathVariable(value = "id") long id, @RequestBody AcceptRequest data) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        authUserOrThrow(order.getAnswerer().getId());
        if (order.getState() != OrderState.REVIEWED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_RESPONDED");
        }
        if (data.isAccept()) {
            order.setState(OrderState.ACCEPTED);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getAnswerExpirationSeconds()));
        } else {
            order.setState(OrderState.REJECTED_BY_ANSWERER);
            userService.refund(order);
        }
        orderService.save(order);
    }

    @PostMapping("/{id}/end")
    public void endChat(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        OrderEndReason reason;
        if (authIsUser(order.getAsker().getId())) {
            reason = OrderEndReason.ASKER;
        } else if (authIsUser(order.getAnswerer().getId())) {
            reason = OrderEndReason.ANSWERER;
        } else {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        if (order.getState() != OrderState.ANSWERED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_ENDED");
        }
        order.setState(OrderState.CHAT_ENDED);
        order.setEndReason(reason);
        order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getFulfillExpirationSeconds()));
        orderService.save(order);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public void cancelOrder(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        User asker = order.getAsker();
        authUserOrThrow(asker.getId());
        if (order.getState() != OrderState.CREATED
                && order.getState() != OrderState.REVIEWED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "CANNOT_CANCEL");
        }
        userService.refund(order);
        order.setState(OrderState.CANCELLED);
        orderService.save(order);
    }

    @PostMapping("/{id}/answer")
    @ResponseStatus(value = HttpStatus.OK)
    public void answerOrder(@PathVariable(value = "id") long id, @RequestBody AnswerRequest request) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        User answerer = order.getAnswerer();
        authUserOrThrow(answerer.getId());
        if (order.getState() != OrderState.ACCEPTED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "CANNOT_ANSWER");
        }
        orderService.answerOrder(order, request.getAnswer());
    }

    @GetMapping
    public OrderListResponse listOrders(
            @RequestParam(required = false) Long asker,
            @RequestParam(required = false) Long answerer,
            @RequestParam(required = false) Boolean finished,
            @RequestParam(required = false) OrderState state,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        authLoginOrThrow();
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.ORDER_LIST_MAX_PAGE_SIZE);
        PageRequest pageRequest = PageRequest.ofSize(pageSize).withPage(page - 1);
        Page<Order> result;
        if (authIsAdmin()) {
            orderService.setPageRequest(pageRequest);
            // state == null 时列出所有订单，包含已删除
            result = orderService.listByState(state);
        } else {
            orderService.setPageRequest(pageRequest.withSort(Sort.by(Sort.Direction.DESC, "createTime")));
            if (asker != null && authIsUser(asker)) {
                // finished == null 时列出所有该用户的订单
                result = orderService.listByAsker(userService.getById(asker), finished);
            } else if (answerer != null && authIsUser(answerer)) {
                // finished == null 时列出所有该用户的订单
                result = orderService.listByAnswerer(userService.getById(answerer), finished);
            } else {
                throw new ApiException(403, ApiException.NO_PERMISSION);
            }
        }
        return new OrderListResponse(result, authIsAdmin() ? 2 : 0);
    }

    private Order getByIdOrThrow(long id, boolean allowDeleted) {
        Optional<Order> order = orderService.findById(id);
        if (order.isEmpty() || (order.get().isDeleted() && !allowDeleted)) {
            throw new ApiException(HttpStatus.NOT_FOUND);
        }
        return order.get();
    }

    private User[] checkOrderDataOrThrow(OrderRequest data) {
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
