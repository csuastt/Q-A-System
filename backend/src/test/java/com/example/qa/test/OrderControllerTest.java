package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.order.exchange.AcceptRequest;
import com.example.qa.order.exchange.AnswerRequest;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.Order;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private static final String description = "Test Description";
    private static MockUtils mockUtils;
    private static long askerId;
    private static long answererId;

    private static String askerToken;
    private static String answererToken;
    private static String superAdminToken;
    private static String adminToken;
    private static String askerToken2;
    private static String answererToken2;


    @BeforeAll
    static void addUsers(@Autowired MockMvc mockMvc,
                         @Autowired UserRepository userRepository,
                         @Autowired AdminRepository adminRepository,
                         @Autowired PasswordEncoder passwordEncoder) throws Exception {
        mockUtils = new MockUtils(mockMvc);

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("testAsker");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();

        registerRequest.setUsername("testAnswerer");
        User answerer = new User(registerRequest);
        answerer.setRole(User.Role.ANSWERER);
        answerer.addRating(1);
        userRepository.save(answerer);
        answererId = answerer.getId();

        registerRequest.setUsername("testAsker2");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker2 = new User(registerRequest);
        userRepository.save(asker2);
//        askerId = asker2.getId();

        registerRequest.setUsername("testAnswerer2");
        User answerer2 = new User(registerRequest);
        answerer2.setRole(User.Role.ANSWERER);
        userRepository.save(answerer2);
//        answererId = answerer2.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(Admin.Role.ADMIN);
        Admin admin = new Admin(adminRequest);
        adminRepository.save(admin);

        LoginRequest userRequest = new LoginRequest();
        userRequest.setUsername("testAsker");
        userRequest.setPassword(password);
        askerToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        userRequest.setUsername("testAnswerer");
        userRequest.setPassword(password);
        answererToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        LoginRequest user2Request = new LoginRequest();
        user2Request.setUsername("testAsker2");
        user2Request.setPassword(password);
        askerToken2 = mockUtils.postAndDeserialize("/api/user/login", askerToken, user2Request, status().isOk(), TokenResponse.class).getToken();

        user2Request.setUsername("testAnswerer2");
        user2Request.setPassword(password);
        answererToken2 = mockUtils.postAndDeserialize("/api/user/login", askerToken, user2Request, status().isOk(), TokenResponse.class).getToken();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();

        AdminRequest request = new AdminRequest();
        request.setUsername("testAdmin" + 1);
        request.setRole(Admin.Role.ADMIN);
        request.setPassword("password");
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        PasswordResponse passwordResponse = mockUtils.postAndDeserialize("/api/admins", superAdminToken, request, status().isOk(),PasswordResponse.class);
        loginRequest.setUsername("testAdmin" + 1);
        loginRequest.setPassword(passwordResponse.getPassword());
        TokenResponse tokenResponse2 = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        adminToken = tokenResponse2.getToken();

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

    @Test
    long createOrderAdmin() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        OrderResponse result = mockUtils.postAndDeserialize("/api/orders", adminToken, request, status().isForbidden(), OrderResponse.class);
        return result.getId();
    }

    @Test
    void createOrderWithProperties() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        request.setEndReason(Order.EndReason.ASKER);
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isOk());
        request.setPrice(-1);
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isOk());
        request.setPrice(10);
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isOk());
        request.setPrice(-1);
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isOk());
        request.setEndReason(null);
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isOk());
        request.setPrice(null);
        OrderResponse result = mockUtils.postAndDeserialize("/api/orders", askerToken, request, status().isOk(), OrderResponse.class);
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        assertEquals(result.getAsker().getId(), askerId);
        assertEquals(result.getAnswerer().getId(), answererId);
        assertEquals(question, result.getQuestionTitle());
        assertEquals(description, result.getQuestionDescription());
        assertEquals(Order.State.CREATED, result.getState());

        request.setPrice(Integer.MAX_VALUE);
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isForbidden());
        // 回答者价格太高的情况
    }

    @Test
    void createOrderWithInvalidAsker() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(Long.MAX_VALUE);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        MvcResult createResult = mockUtils.postUrl("/api/orders", askerToken, request, status().isOk());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isForbidden());
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
    }

    @Test
    void createOrderWithInvalidAnswerer() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(askerId);
        request.setTitle(question);
        request.setDescription(description);
        MvcResult createResult = mockUtils.postUrl("/api/orders", askerToken, request, status().isForbidden());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isForbidden());
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
    }

    @Test
    void createOrderWithInvalidQuestion() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle("q");
        request.setDescription("d");
        MvcResult createResult = mockUtils.postUrl("/api/orders", askerToken, request, status().isForbidden());
        mockUtils.postUrl("/api/orders", superAdminToken, request, status().isForbidden());
        mockUtils.postUrl("/api/orders", null, request, status().isUnauthorized());
    }

    @Test
    void deleteOrder() throws Exception {
        long id = createOrder();
        mockUtils.deleteUrl("/api/orders/" + id, askerToken, null, status().isForbidden());
        mockUtils.deleteUrl("/api/orders/" + id, superAdminToken, null, status().isOk());
        mockUtils.deleteUrl("/api/orders/" + id, superAdminToken, null, status().isForbidden());
        mockUtils.deleteUrl("/api/orders/" + id, null, null, status().isUnauthorized());
    }

    @Test
    void queryOrder() throws Exception {
        query(createOrder());
    }

    @Test
    void queryInvalidOrder() throws Exception {
        mockUtils.getUrl("/api/orders/" + -1, askerToken, null, null, status().isNotFound());
        mockUtils.getUrl("/api/orders/" + -1, null, null, null, status().isNotFound());
    }

    @Test
    void editOrder() throws Exception {
        long id = createOrder();
        String newQuestion = "NewQuestion";
        OrderRequest request = new OrderRequest();
        request.setTitle(newQuestion);
        request.setEndReason(Order.EndReason.ASKER);
        edit(id, request);
        request.setPrice(10);
        edit(id, request);
        request.setPrice(-1);
        edit(id, request);
        request.setEndReason(null);
        edit(id, request);
        request.setPrice(null);
        edit(id, request);
        assertEquals(query(id).getQuestionTitle(), newQuestion);
    }

    @Test
    void reviewOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.CREATED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/review", superAdminToken, accept, status().isOk());
        mockUtils.postUrl("/api/orders/" + id + "/review", superAdminToken, accept, status().isForbidden());
        // 普通管理员失败测例
        mockUtils.postUrl("/api/orders/" + id + "/review", adminToken, accept, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/review", askerToken, accept, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/review", null, accept, status().isUnauthorized());
        assertEquals(Order.State.REVIEWED, query(id).getState());
    }
    @Test
    void reviewNotOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.CREATED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(false);
        mockUtils.postUrl("/api/orders/" + id + "/review", superAdminToken, accept, status().isOk());
        // 普通管理员失败测例
        mockUtils.postUrl("/api/orders/" + id + "/review", adminToken, accept, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/review", askerToken, accept, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/review", null, accept, status().isUnauthorized());
    }
    @Test
    void answerOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.CREATED);
        edit(id, request);
        AnswerRequest answer = new AnswerRequest();
        answer.setAnswer("This is an answer");
        mockUtils.postUrl("/api/orders/" + id + "/answer", answererToken, answer, status().isForbidden());
        request.setState(Order.State.ACCEPTED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/answer", answererToken, answer, status().isOk());
        mockUtils.postUrl("/api/orders/" + id + "/answer", answererToken2, answer, status().isForbidden());
    }

    @Test
    void reviewInvalidOrder() throws Exception {
        long id = createOrder();
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/review", null, accept, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/review", askerToken, accept, status().isForbidden());
    }

    @Test
    void respondOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.REVIEWED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/respond", null, accept, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/respond", answererToken, accept, status().isOk());
        assertEquals(Order.State.ACCEPTED, query(id).getState());
    }

    @Test
    void noRespondOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.REVIEWED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(false);
        mockUtils.postUrl("/api/orders/" + id + "/respond", null, accept, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/respond", answererToken, accept, status().isOk());
        assertEquals(Order.State.REJECTED_BY_ANSWERER, query(id).getState());
    }


    @Test
    void respondInvalidOrder() throws Exception {
        long id = createOrder();
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/respond", null, accept, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/respond", answererToken, accept, status().isForbidden());
    }

    @Test
    void endOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.ANSWERED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/end", answererToken2, null, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/end", askerToken2, null, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/end", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/end", askerToken, null, status().isOk());
        assertEquals(Order.State.CHAT_ENDED, query(id).getState());
    }

    @Test
    void endOrderAnswer() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.ANSWERED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/end", null, null, status().isUnauthorized());
//        mockUtils.postUrl("/api/orders/" + id + "/end", askerToken, null, status().isOk());
        mockUtils.postUrl("/api/orders/" + id + "/end", answererToken, null, status().isOk());
        assertEquals(Order.State.CHAT_ENDED, query(id).getState());
    }

    @Test
    void endInvalidOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/end", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/end", askerToken, null, status().isForbidden());
    }

    @Test
    void cancelOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/cancel", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/cancel", askerToken, null, status().isOk());
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.REVIEWED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/cancel", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/cancel", askerToken, null, status().isOk());
        assertEquals(Order.State.CANCELLED, query(id).getState());
    }

    @Test
    void cancelInvalidOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/cancel", null, null, status().isUnauthorized());
        OrderRequest request = new OrderRequest();
        request.setState(Order.State.ACCEPTED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/cancel", askerToken, null, status().isForbidden());
    }

    @Test
    void queryOrderList() throws Exception {
        createOrder();
        mockUtils.getUrl("/api/orders?asker=" + askerId, askerToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?asker=" + askerId + " &reviewed=true", askerToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?asker=" + Long.MAX_VALUE, askerToken, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders?answerer=" + askerId, askerToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?answerer=" + Long.MAX_VALUE + " &reviewed=true", askerToken, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders", askerToken, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders", null, null, null, status().isUnauthorized());
        mockUtils.getUrl("/api/orders?showPublic=1", askerToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?showPublic=1&keyword=为什么", askerToken, null, null, status().isOk());
    }

    OrderResponse query(long id) throws Exception {
        OrderResponse result = mockUtils.getAndDeserialize("/api/orders/" + id, askerToken, null, null, status().isOk(), OrderResponse.class);
        mockUtils.getUrl("/api/orders/" + id, answererToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders/" + id, answererToken2, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders/" + id, askerToken2, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders/" + id, adminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders/" + id, superAdminToken, null, null, status().isOk());
        assertEquals(result.getId(), id);
        return result;
    }

    void edit(long id, OrderRequest data) throws Exception {
        mockUtils.putUrl("/api/orders/" + id, superAdminToken, data, status().isOk());
    }

}
