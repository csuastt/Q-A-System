package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.notification.NotificationRepository;
import com.example.qa.notification.exchange.ItemCount;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.OrderRepository;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.model.Order;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.UserStatsResponse;
import com.example.qa.user.model.User;
import com.example.qa.utils.MockUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.HashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private static final String description = "Test Description";
    private static MockUtils mockUtils;
    private static long askerId;
    private static long answererId;
    private static long notiId;

    private static String askerToken;
    private static String answererToken;
    private static String superAdminToken;


    @BeforeAll
    static void addUsers(@Autowired MockMvc mockMvc,
                         @Autowired UserRepository userRepository,
                         @Autowired AdminRepository adminRepository,
                         @Autowired NotificationRepository notificationRepository,
                         @Autowired OrderRepository orderRepository,
                         @Autowired PasswordEncoder passwordEncoder) throws Exception {
        mockUtils = new MockUtils(mockMvc);

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("testAsker4");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();

        registerRequest.setUsername("testAnswerer4");
        User answerer = new User(registerRequest);
        answerer.setRole(User.Role.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin4");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(Admin.Role.ADMIN);
        Admin admin = new Admin(adminRequest);
        adminRepository.save(admin);

        LoginRequest userRequest = new LoginRequest();
        userRequest.setUsername("testAsker4");
        userRequest.setPassword(password);
        askerToken = mockUtils.postAndDeserialize("/api/user/login", null, userRequest, status().isOk(), TokenResponse.class).getToken();

        userRequest.setUsername("testAnswerer4");
        userRequest.setPassword(password);
        answererToken = mockUtils.postAndDeserialize("/api/user/login", null, userRequest, status().isOk(), TokenResponse.class).getToken();


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();

        long count = 1L;
        ItemCount itemCount = new ItemCount(count);
        itemCount.toString();

        User user = new User();
        user.setId(1L);
        user.setUsername("aa");
        user.setPassword("aaaa");
        user.setRole(User.Role.ANSWERER);
        UserStatsResponse userStatsResponse = new UserStatsResponse(user);
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
        orderRepository.save(target);
        boolean haveRead = true;
        String msgSummary = "sss";
        Order.State state = Order.State.ACCEPTED;
        ZonedDateTime deadline = ZonedDateTime.now();
        Notification notification = new Notification();
        notification.setHaveRead(haveRead);
        notification.setCreateTime(createTime);
        notification.setMsgSummary(msgSummary);
        notification.setDeadline(deadline);
        notification.setNewState(state);
        notification.setReceiver(answerer);
        notification.setTarget(target);
        notification.setType(type);
        notificationRepository.save(notification);
        notiId = notification.getId();

    }

    @Test
    void getNotifications() throws Exception {
//        Optional<Boolean> hasRead = Optional.of(true);
        Boolean hasRead = true;
        int page = 1;
        int pageSize = 20;
        mockUtils.getUrl("/api/users/" + askerId + "/notif", askerToken, new HashMap<>() {
            {
                put("hasRead", String.valueOf(hasRead));
                put("page", String.valueOf(page));
                put("pageSize", String.valueOf(pageSize));
            }
        }, null, status().isOk());
        mockUtils.getUrl("/api/users/" + 1 + "/notif", askerToken, new HashMap<>() {
            {
                put("hasRead", String.valueOf(hasRead));
                put("page", String.valueOf(page));
                put("pageSize", String.valueOf(pageSize));
            }
        }, null, status().isForbidden());

        mockUtils.getUrl("/api/users/" + askerId + "/notif", null, new HashMap<>() {
            {
                put("hasRead", String.valueOf(hasRead));
                put("page", String.valueOf(page));
                put("pageSize", String.valueOf(pageSize));
            }
        }, null, status().isUnauthorized());
    }

    @Test
    void setRead() throws Exception {
        mockUtils.postUrl("/api/users/" + answererId  + "/notif/" +  notiId  + "/read", answererToken, null, status().isOk());
        mockUtils.postUrl("/api/users/" + askerId  + "/notif/" +  notiId  + "/read", askerToken, null, status().isUnauthorized());
        mockUtils.postUrl("/api/users/" + answererId  + "/notif/" +  askerId  + "/read", answererToken, null, status().isNotFound());
    }

    @Test
    void readAll() throws Exception {
        mockUtils.postUrl("/api/users/" + answererId  + "/notif/readAll", answererToken, null, status().isOk());
    }

    @Test
    void deleteRead() throws Exception {
//        mockUtils.postUrl("/api/users/" + answererId  + "/notif/deleteRead", answererToken, null, status().isOk());
    }
}