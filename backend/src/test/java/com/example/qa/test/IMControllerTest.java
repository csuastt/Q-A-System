package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.im.IMService;
import com.example.qa.notification.NotificationRepository;
import com.example.qa.order.OrderRepository;
import com.example.qa.order.OrderService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IMControllerTest {


    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private static final String description = "Test Description";
    private static MockUtils mockUtils;
    private static long askerId;
    private static long answererId;
    private final IMService imService;
    private final OrderService orderService;
    private final NotificationRepository noRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final AdminRepository adminRepo;

    private static String askerToken;
    private static String answererToken;
    private static long askerId2;
    private static long answererId2;
    private static String superAdminToken;

    private static User asker;
    private static User answerer;
    private static String askerToken2;
    private static String answererToken2;


    IMControllerTest(@Autowired IMService imService, @Autowired OrderService orderService, @Autowired NotificationRepository notificationRepository,
                     @Autowired OrderRepository orderRepository,
                     @Autowired UserRepository userRepository,
                     @Autowired AdminRepository adminRepository) {
        this.imService = imService;
        this.orderService = orderService;
        noRepo = notificationRepository;
        orderRepo = orderRepository;
        userRepo = userRepository;
        adminRepo = adminRepository;
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

    @BeforeAll
    static void addUsers(
            @Autowired MockMvc mockMvc,
                         @Autowired UserRepository userRepository,
                         @Autowired AdminRepository adminRepository,
                         @Autowired PasswordEncoder passwordEncoder) throws Exception {
        mockUtils = new MockUtils(mockMvc);
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("testAsker5");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();

        registerRequest.setUsername("testAnswerer5");
        answerer = new User(registerRequest);
        answerer.setRole(UserRole.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin5");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(AdminRole.ADMIN);
        Admin admin = new Admin(adminRequest);
        adminRepository.save(admin);

        registerRequest.setUsername("testAsker6");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker2 = new User(registerRequest);
        userRepository.save(asker2);
        askerId2 = asker2.getId();

        registerRequest.setUsername("testAnswerer6");
        User answerer2 = new User(registerRequest);
        answerer2.setRole(UserRole.ANSWERER);
        userRepository.save(answerer2);
        answererId2 = answerer2.getId();

        LoginRequest userRequest = new LoginRequest();
        userRequest.setUsername("testAsker5");
        userRequest.setPassword(password);
        askerToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        userRequest.setUsername("testAnswerer5");
        userRequest.setPassword(password);
        answererToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        LoginRequest user2Request = new LoginRequest();
        user2Request.setUsername("testAsker6");
        user2Request.setPassword(password);
        askerToken2 = mockUtils.postAndDeserialize("/api/user/login", askerToken, user2Request, status().isOk(), TokenResponse.class).getToken();

        user2Request.setUsername("testAnswerer6");
        user2Request.setPassword(password);
        answererToken2 = mockUtils.postAndDeserialize("/api/user/login", askerToken, user2Request, status().isOk(), TokenResponse.class).getToken();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();
    }

    @Test
    void sendMessage() throws Exception {
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
        orderRepo.save(order);
        imService.sendFromUser(order, asker, "1234567");
        Order order2 = new Order(request, asker, answerer, true);
        orderRepo.save(order2);
        imService.getOrderHistoryMessages(order);
        imService.getOrderHistoryMessages(order.getId());


        orderService.listByAnswerer(answerer, true);
        orderService.listByAnswerer(answerer, false);

        orderService.listByAsker(asker, true);
        orderService.listByAsker(asker, false);
        orderService.listByReviewed();
        orderService.clearExpirations();

        order.setState(OrderState.ACCEPTED);
        orderRepo.save(order);
        orderService.handleExpiration(order);

        order.setState(OrderState.REVIEWED);
        orderRepo.save(order);
        orderService.handleExpiration(order);

        order.setState(OrderState.ANSWERED);
        orderRepo.save(order);
        orderService.handleExpiration(order);

        order.setState(OrderState.CHAT_ENDED);
        orderRepo.save(order);
        orderService.handleExpiration(order);

        order.setState(OrderState.CANCELLED);
        orderRepo.save(order);
        orderService.handleExpiration(order);

        mockUtils.getUrl("/im/history/" + order.getId(), askerToken, null,null,  status().isOk());
        mockUtils.getUrl("/im/history/" + order.getId(), answererToken, null,null,  status().isOk());

        mockUtils.getUrl("/im/history/" + order.getId(), askerToken2, null, null, status().isForbidden());
        mockUtils.getUrl("/im/history/" + order.getId(), null, null,null,  status().isUnauthorized());
        mockUtils.getUrl("/im/history/" + 1000, askerToken, null,null,  status().isNotFound());
    }
}