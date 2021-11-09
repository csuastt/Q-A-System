package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.errorhandling.MessageException;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.im.exchange.MessagePayload;
import com.example.qa.im.model.Message;
import com.example.qa.notification.NotificationRepository;
import com.example.qa.notification.exchange.NotifPayload;
import com.example.qa.notification.exchange.PagedList;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.MockUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IMBeanTest {
    @Autowired
    private final NotificationRepository repo;
    private static long askerId;
    private static long answererId;

    private static String askerToken;
    private static String answererToken;
    private static String superAdminToken;
    private static MockUtils mockUtils;
    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private static final String description = "Test Description";
    private static User asker;
    private static User answerer;

    IMBeanTest(@Autowired NotificationRepository repo) {
        this.repo = repo;
    }

    @Test
    void testForMessageException(){
        MessageException exception = new MessageException();
        MessageException exception1 = new MessageException("1");
        MessageException exception2 = new MessageException(HttpStatus.FORBIDDEN);
        MessageException exception3 = new MessageException(HttpStatus.FORBIDDEN, "1");
    }

    @BeforeAll
    static void addUsers(@Autowired MockMvc mockMvc,
                         @Autowired UserRepository userRepository,
                         @Autowired AdminRepository adminRepository,
                         @Autowired PasswordEncoder passwordEncoder) throws Exception {
        mockUtils = new MockUtils(mockMvc);

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("testAsker3");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();

        registerRequest.setUsername("testAnswerer3");
        answerer = new User(registerRequest);
        answerer.setRole(UserRole.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin3");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(AdminRole.ADMIN);
        Admin admin = new Admin(adminRequest);
        adminRepository.save(admin);

        LoginRequest userRequest = new LoginRequest();
        userRequest.setUsername("testAsker3");
        userRequest.setPassword(password);
        askerToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        userRequest.setUsername("testAnswerer3");
        userRequest.setPassword(password);
        answererToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();

    }

    @Test
    void testForMessagePayload(){
        User user = new User();
        user.setId(1L);
        user.setUsername("aa");
        user.setPassword("aaaa");
        user.setRole(UserRole.ANSWERER);
        Message message = new Message();
        message.setSendTime(ZonedDateTime.now());
        message.setBody("1");
        message.setSender(user);
        MessagePayload message2 = new MessagePayload(message);
    }
    @Test
    void notificationTest(){
        User user = new User();
        user.setId(1L);
        user.setUsername("aa");
        user.setPassword("aaaa");
        user.setRole(UserRole.ANSWERER);
        long id = 1L;
        ZonedDateTime createTime = ZonedDateTime.now();
        Notification.Type type = Notification.Type.ACCEPT_DEADLINE;
        User receiver = user;
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        Order target = new Order(request, asker, answerer, true);
        boolean haveRead = true;
        String msgSummary = "sss";
        OrderState state = OrderState.ACCEPTED;
        ZonedDateTime deadline = ZonedDateTime.now();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setHaveRead(haveRead);
        notification.setCreateTime(createTime);
        notification.setMsgSummary(msgSummary);
        notification.setDeadline(deadline);
        notification.setNewState(state);
        notification.setReceiver(answerer);
        notification.setTarget(target);
        notification.setType(type);

        Notification.ofPlain(answerer, "111");
        Notification.ofNewMessage(answerer, target, "111");
        Notification.ofOrderStateChanged(answerer, target);
        Notification.ofDeadlineOrTimeout(answerer, target, type, deadline);
        type = Notification.Type.NEW_MESSAGE;
        try {
            Notification.ofDeadlineOrTimeout(answerer, target, type, deadline);
        } catch (IllegalArgumentException exception){}
        notification.toString();

        NotifPayload payload = new NotifPayload(notification);

    }

    @Test
    void testForPageList(){
        Optional<Boolean> hasRead = Optional.of(true);
        var pageable = PageRequest.of(2, 2);
        PagedList<Notification> list = new PagedList<Notification>(hasRead
                .map(hasReadValue -> repo.findByReceiverAndHaveReadOrderByCreateTimeDesc(asker, hasReadValue, pageable))
                .orElseGet(() -> repo.findByReceiverOrderByCreateTimeDesc(asker, pageable)));

        list.map(new Function<Notification, String>() {
            @Override
            public String apply(Notification s) {
                return"11111";
            }
        });
    }

    @Test
    void testForMessage(){

        User user = new User();
        user.setId(1L);
        user.setUsername("aa");
        user.setPassword("aaaa");
        user.setRole(UserRole.ANSWERER);
        long id = 1L;
        ZonedDateTime sendTime = ZonedDateTime.now();
        User sender = user;
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        Order order = new Order(request, asker, answerer, true);
        String body = "sss";
        Message message = new Message();
        Message message2 = new Message(id, sendTime, order, sender, body);
        message.setId(id);
        message.setOrder(order);
        message.setSendTime(sendTime);
        message.setSender(sender);
        message.setBody(body);
        message.toString();
        Message.builder();
    }

    @Test
    long createOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        OrderResponse result = mockUtils.postAndDeserialize("/api/orders", askerToken, request, status().isOk(), OrderResponse.class);
        mockUtils.postUrl("/api/orders",answererToken, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",answererToken2, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",askerToken2, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",superAdminToken, request, status().isOk());
//        mockUtils.postUrl("/api/orders",adminToken, request, status().isOk());

        return result.getId();
    }

}
