package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.im.IMService;
import com.example.qa.notification.NotificationRepository;
import com.example.qa.order.OrderRepository;
import com.example.qa.order.OrderService;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.Order;
import com.example.qa.order.storage.FileSystemStorageService;
import com.example.qa.order.storage.StorageProperties;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.model.User;
import com.example.qa.utils.MockUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.UUID;

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
    private static String askerToken;
    private static String answererToken;
    private static long askerId2;
    private static long answererId2;
    private static String superAdminToken;
    private static User asker;
    private static User answerer;
    private static String askerToken2;
    private static String answererToken2;
    private final IMService imService;
    private final OrderService orderService;
    private final FileSystemStorageService storageService;
    private final NotificationRepository noRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final AdminRepository adminRepo;


    IMControllerTest(@Autowired IMService imService, @Autowired OrderService orderService, @Autowired FileSystemStorageService storageService, @Autowired NotificationRepository notificationRepository,
                     @Autowired OrderRepository orderRepository,
                     @Autowired UserRepository userRepository,
                     @Autowired AdminRepository adminRepository) {
        this.imService = imService;
        this.orderService = orderService;
        this.storageService = storageService;
        noRepo = notificationRepository;
        orderRepo = orderRepository;
        userRepo = userRepository;
        adminRepo = adminRepository;
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
        answerer.setRole(User.Role.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin5");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(Admin.Role.ADMIN);
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
        answerer2.setRole(User.Role.ANSWERER);
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
    long createOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        OrderResponse result = mockUtils.postAndDeserialize("/api/orders", askerToken, request, status().isOk(), OrderResponse.class);
        mockUtils.postUrl("/api/orders", answererToken, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",answererToken2, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",askerToken2, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",superAdminToken, request, status().isOk());
//        mockUtils.postUrl("/api/orders",adminToken, request, status().isOk());

        return result.getId();
    }

    @Test
    void sendMessage() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("aa");
        user.setPassword("aaaa");
        user.setRole(User.Role.ANSWERER);
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        Order order = new Order(request, asker, answerer, true);
        orderRepo.save(order);
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

        order.setState(Order.State.ACCEPTED);
        orderRepo.save(order);

        order.setState(Order.State.REVIEWED);
        orderRepo.save(order);

        order.setState(Order.State.ANSWERED);
        orderRepo.save(order);
        imService.sendFromUser(order, asker, ZonedDateTime.now(), "1234567");

        order.setState(Order.State.CHAT_ENDED);
        orderRepo.save(order);
        imService.sendFromUser(order, asker, ZonedDateTime.now(), "1234567");

        order.setState(Order.State.CANCELLED);
        orderRepo.save(order);

        storageService.init();
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        storageService.store(file, uuid);
        storageService.load(uuid);
        storageService.loadAll();
        storageService.getNameByUUID(uuid);
        storageService.loadAsResource(uuid);
        storageService.delete(uuid);
        storageService.deleteAll();
        storageService.getNameByUUID(uuid);

        try{
            storageService.load(uuid2);
        }catch (Exception exception){}

        try{
            storageService.loadAsResource(uuid2);
        }catch (Exception exception){}

        try{
            storageService.delete(uuid2);
        }catch (Exception exception){}

        try{
            storageService.getNameByUUID(uuid2);
        }catch (Exception exception){}

        StorageProperties properties = new StorageProperties();
        properties.setLocation("1234");

        mockUtils.getUrl("/api/im/history/" + order.getId(), askerToken, null, null, status().isOk());
        mockUtils.getUrl("/api/im/history/" + order.getId(), answererToken, null, null, status().isOk());

        mockUtils.getUrl("/api/im/history/" + order.getId(), askerToken2, null, null, status().isForbidden());
        mockUtils.getUrl("/api/im/history/" + order.getId(), null, null, null, status().isUnauthorized());
        mockUtils.getUrl("/api/im/history/" + 1000, askerToken, null, null, status().isNotFound());
    }
}