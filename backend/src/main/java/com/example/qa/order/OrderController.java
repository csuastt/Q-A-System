package com.example.qa.order;

import com.example.qa.admin.AdminService;
import com.example.qa.admin.model.Admin;
import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ValueRequest;
import com.example.qa.im.IMService;
import com.example.qa.notification.NotificationService;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.exchange.*;
import com.example.qa.order.model.Attachment;
import com.example.qa.order.model.Order;
import com.example.qa.order.storage.StorageService;
import com.example.qa.user.UserService;
import com.example.qa.user.model.User;
import com.example.qa.utils.FieldValidator;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.example.qa.security.RestControllerAuthUtils.*;
import static com.example.qa.utils.ReflectionUtils.hasField;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final AdminService adminService;
    private final StorageService storageService;
    private final NotificationService notificationService;
    private final IMService imService;

    public OrderController(UserService userService, OrderService orderService, AdminService adminService, StorageService storageService, NotificationService notificationService, IMService imService) {
        this.userService = userService;
        this.orderService = orderService;
        this.adminService = adminService;
        this.storageService = storageService;
        this.notificationService = notificationService;
        this.imService = imService;
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
        int price = isAdmin ? Objects.requireNonNullElse(data.getPrice(), answerer.getPrice()) : answerer.getPrice();
        if (asker.getBalance() < price) {
            throw new ApiException(403, "BALANCE_NOT_ENOUGH");
        }
        asker.setBalance(asker.getBalance() - price);
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
        boolean isAdmin = authLogin() && authIsAdmin();
        Order order = getByIdOrThrow(id, isAdmin);
        if (!order.isShowPublic()) {
            authLoginOrThrow();
            long userId = authGetId();
            if (!isAdmin && order.getAsker().getId() != userId && order.getAnswerer().getId() != userId) {
                throw new ApiException(403, ApiException.NO_PERMISSION);
            }
        }
        return new OrderResponse(order, isAdmin ? 2 : 1);
    }

    @GetMapping("/{id}/attachments")
    public List<Attachment> queryAttachmentList(@PathVariable(value = "id") long id) {
        Order order = getByIdOrThrow(id, false);
        return order.getAttachmentList();
    }

    @GetMapping("/{id}/attachments/{uuid}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable(value = "id") long id, @PathVariable(value = "uuid") UUID uuid) {
        Resource file = storageService.loadAsResource(uuid);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + storageService.getNameByUUID(uuid)).body(file);
    }

    @PostMapping("/{id}/attachments")
    @ResponseBody
    public Attachment uploadFile(@PathVariable(value = "id") long id, @RequestParam(value = "file") MultipartFile multipartFile) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        if (!authIsAdmin() && order.getAsker().getId() != authGetId() && order.getAnswerer().getId() != authGetId()) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        Attachment attachment = new Attachment(multipartFile);
        order.getAttachmentList().add(attachment);
        orderService.save(order);
        storageService.store(multipartFile, attachment.getUuid());
        return attachment;
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
        if (!authIsAdmin() || adminService.getById(authGetId(), false).getRole() == Admin.Role.ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        Order order = getByIdOrThrow(id, false);
        if (order.getState() != Order.State.CREATED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_REVIEWED");
        }
        if (data.isAccept()) {
            order.setState(Order.State.REVIEWED);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getRespondExpirationSeconds()));
        } else {
            order.setState(Order.State.REJECTED_BY_REVIEWER);
            userService.refund(order);
        }
        order.setReviewed(true);
        order = orderService.save(order);
        notificationService.send(Notification.ofOrderStateChanged(order.getAsker(), order));
        notificationService.send(Notification.ofOrderStateChanged(order.getAnswerer(), order));
    }

    @PostMapping("/{id}/respond")
    public void respondOrder(@PathVariable(value = "id") long id, @RequestBody AcceptRequest data) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        authUserOrThrow(order.getAnswerer().getId());
        if (order.getState() != Order.State.REVIEWED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_RESPONDED");
        }
        if (data.isAccept()) {
            order.setState(Order.State.ACCEPTED);
            order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getAnswerExpirationSeconds()));
        } else {
            order.setState(Order.State.REJECTED_BY_ANSWERER);
            userService.refund(order);
        }
        order = orderService.save(order);
        notificationService.send(Notification.ofOrderStateChanged(order.getAsker(), order));
    }

    @PostMapping("/{id}/end")
    public void endChat(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        Order.EndReason reason;
        if (authIsUser(order.getAsker().getId())) {
            reason = Order.EndReason.ASKER;
        } else if (authIsUser(order.getAnswerer().getId())) {
            reason = Order.EndReason.ANSWERER;
        } else {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        if (order.getState() != Order.State.ANSWERED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_TO_BE_ENDED");
        }
        order.setState(Order.State.CHAT_ENDED);
        order.setEndReason(reason);
        order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getFulfillExpirationSeconds()));
        order = orderService.save(order);
        notificationService.send(Notification.ofOrderStateChanged(order.getAsker(), order));
        notificationService.send(Notification.ofOrderStateChanged(order.getAnswerer(), order));
        imService.sendFromSystem(order, (reason == Order.EndReason.ASKER ? "提问者" : "回答者") + "已结束聊天");
    }

    @PostMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        User asker = order.getAsker();
        authUserOrThrow(asker.getId());
        if (order.getState() != Order.State.CREATED
                && order.getState() != Order.State.REVIEWED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "CANNOT_CANCEL");
        }
        userService.refund(order);
        order.setState(Order.State.CANCELLED);
        orderService.save(order);
    }

    @PostMapping("/{id}/answer")
    public void answerOrder(@PathVariable(value = "id") long id, @RequestBody AnswerRequest request) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        User answerer = order.getAnswerer();
        authUserOrThrow(answerer.getId());
        if (order.getState() != Order.State.ACCEPTED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "CANNOT_ANSWER");
        }
        order.setAnswer(request.getAnswer());
        order.setState(Order.State.ANSWERED);
        order.setExpireTime(ZonedDateTime.now().plusSeconds(SystemConfig.getMaxChatTimeSeconds()));
        order = orderService.save(order);
        answerer.setAnswerCount(answerer.getAnswerCount() + 1);
        userService.save(answerer);
        User asker = order.getAsker();
        asker.setAskCount(asker.getAskCount() + 1);
        userService.save(asker);
        notificationService.send(Notification.ofOrderStateChanged(order.getAsker(), order));
    }

    @PostMapping("/{id}/rate")
    public void rateOrder(@PathVariable(value = "id") long id, @RequestBody ValueRequest request) {
        authLoginOrThrow();
        Order order = getByIdOrThrow(id, false);
        authUserOrThrow(order.getAsker().getId());
        if (order.getRating() > 0 || !Order.State.completedOrderStates.contains(order.getState())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "CANNOT_RATE");
        }
        order.setRating(request.getValue());
        order = orderService.save(order);
        User answerer = order.getAnswerer();
        answerer.addRating(request.getValue());
        userService.save(answerer);
    }

    @GetMapping
    public OrderListResponse listOrders(
            @RequestParam(required = false) Boolean showPublic,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long asker,
            @RequestParam(required = false) Long answerer,
            @RequestParam(required = false) Boolean finished,
            @RequestParam(required = false) Boolean reviewed,
            @RequestParam(required = false) List<Order.State> state,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Sort.Direction sortDirection,
            @RequestParam(required = false) String sortProperty
    ) {
        long startTime = System.currentTimeMillis();
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.ORDER_LIST_MAX_PAGE_SIZE);
        if (!hasField(Order.class, sortProperty)) {
            sortProperty = "createTime";
        }
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize,
                Objects.requireNonNullElse(sortDirection, Sort.Direction.DESC), sortProperty);
        orderService.setPageRequest(pageRequest);
        Page<Order> result;
        if (Boolean.TRUE.equals(showPublic)) {
            // keyword == null 时列出所有公开订单
            result = orderService.listByPublic(keyword);
            OrderListResponse response = new OrderListResponse(result, 0);
            response.setTimeMillis(System.currentTimeMillis() - startTime);
            return response;
        }
        authLoginOrThrow();
        int orderResponseLevel = 0;
        if (authIsAdmin()) {
            pageRequest = pageRequest.withSort(Objects.requireNonNullElse(sortDirection, Sort.Direction.ASC), sortProperty);
            orderService.setPageRequest(pageRequest);
            if (Boolean.TRUE.equals(reviewed)) {
                result = orderService.listByReviewed();
            } else {
                // state == null 时列出所有订单，包含已删除
                result = orderService.listByState(state);
            }
            orderResponseLevel = 2;
        } else {
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
        OrderListResponse response = new OrderListResponse(result, orderResponseLevel);
        response.setTimeMillis(System.currentTimeMillis() - startTime);
        return response;
    }

    private Order getByIdOrThrow(long id, boolean allowDeleted) {
        Optional<Order> order = orderService.findById(id);
        if (order.isEmpty() || (order.get().isDeleted() && !allowDeleted)) {
            throw new ApiException(HttpStatus.NOT_FOUND);
        }
        return order.get();
    }

    private User[] checkOrderDataOrThrow(OrderRequest data) {
        if (data.getAsker() == null || data.getAnswerer() == null || data.getDescription() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST);
        }
        long newAsker = data.getAsker();
        if (!userService.existsById(newAsker)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ASKER_INVALID");
        }
        long newAnswerer = data.getAnswerer();
        if (newAsker == newAnswerer
                || !userService.existsById(newAnswerer)
                || userService.getById(newAnswerer).getRole() != User.Role.ANSWERER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ANSWERER_INVALID");
        }
        if (!FieldValidator.length
                (data.getTitle(), SystemConfig.QUESTION_MIN_LENGTH, SystemConfig.QUESTION_MAX_LENGTH)
        ) {
            throw new ApiException(HttpStatus.FORBIDDEN, "QUESTION_INVALID");
        }
        return new User[]{userService.getById(newAsker), userService.getById(newAnswerer)};
    }
}
