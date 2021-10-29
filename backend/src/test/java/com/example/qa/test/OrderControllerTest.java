package com.example.qa.test;

import com.example.qa.order.exchange.AcceptRequest;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.UserRepository;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.MockUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    private static MockUtils mockUtils;
    private String token;
    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private static final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private static long askerId;
    private static long answererId;

    @BeforeAll
    static void addUsers(@Autowired UserRepository userRepository, @Autowired PasswordEncoder passwordEncoder, @Autowired MockMvc mockMvc){
        mockUtils = new MockUtils(mockMvc, mapper);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testAsker");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();
        registerRequest.setUsername("testAnswerer");
        User answerer = new User(registerRequest);
        answerer.setRole(UserRole.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();
    }

    @BeforeEach
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testAsker");
        loginRequest.setPassword(password);
        MvcResult loginResult = mockUtils.postUrl("/api/user/login", null, loginRequest, status().isOk());
        TokenResponse response = mapper.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);
        this.token = response.getToken();
    }

    @Test
    long createOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setQuestion(question);
        MvcResult createResult = mockUtils.postUrl("/api/orders", token, request, status().isOk());
        OrderResponse result = mapper.readValue(createResult.getResponse().getContentAsString(), OrderResponse.class);
        assertEquals(result.getAsker().getId(), askerId);
        assertEquals(result.getAnswerer().getId(), answererId);
        assertEquals(question, result.getQuestion());
        assertEquals(OrderState.CREATED, result.getState());
        return result.getId();
    }

    @Test
    void createOrderWithInvalidAskerByUserButNeedToChangeToAdmin() throws Exception {
        // 需要改成管理员登录
        OrderRequest request = new OrderRequest();
        request.setAsker(Long.MAX_VALUE);
        request.setAnswerer(answererId);
        request.setQuestion(question);
        MvcResult createResult = mockUtils.postUrl("/api/orders", token, request, status().isOk());
    }

    @Test
    void createOrderWithInvalidAnswerer() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(askerId);
        request.setQuestion(question);
        MvcResult createResult = mockUtils.postUrl("/api/orders", token, request, status().isForbidden());
    }

    @Test
    void createOrderWithInvalidQuestion() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setQuestion("q");
        MvcResult createResult = mockUtils.postUrl("/api/orders", token, request, status().isForbidden());
    }

    @Test
    void deleteOrder() throws Exception {
        long id = createOrder();
        mockUtils.deleteUrl("/api/orders/" + id, token, null, status().isOk());
        mockUtils.deleteUrl("/api/orders/" + id, token, null, status().isForbidden());
    }

    @Test
    void queryOrder() throws Exception {
        query(createOrder());
    }

    @Test
    void queryInvalidOrder() throws Exception {
        mockUtils.getUrl("/api/orders/" + -1, token, null,null,status().isNotFound());
    }

    @Test
    void editOrder() throws Exception {
        long id = createOrder();
        String newQuestion = "NewQuestion";
        OrderRequest request = new OrderRequest();
        request.setQuestion(newQuestion);
        edit(id, request);
        assertEquals(query(id).getQuestion(), newQuestion);
    }

    @Test
    void reviewOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.PAYED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/review", token, accept, status().isOk());
        assertEquals(OrderState.REVIEWED, query(id).getState());
    }

    @Test
    void reviewInvalidOrder() throws Exception {
        long id = createOrder();
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/review", token, accept, status().isForbidden());
    }

    @Test
    void respondOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.REVIEWED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/respond", token, accept, status().isOk());
        assertEquals(OrderState.ACCEPTED, query(id).getState());
    }

    @Test
    void respondInvalidOrder() throws Exception {
        long id = createOrder();
        AcceptRequest accept = new AcceptRequest(true);
        mockUtils.postUrl("/api/orders/" + id + "/respond", token, accept, status().isForbidden());
    }

    @Test
    void payOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/pay", token, null, status().isOk());
        assertEquals(OrderState.PAYED, query(id).getState());
    }

    @Test
    void payInvalidOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/pay", null,null, status().isUnauthorized());
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.REVIEWED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/pay", token, null, status().isForbidden());
        request.setState(OrderState.CREATED);
        request.setPrice(Integer.MAX_VALUE);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/pay", token, null, status().isForbidden());
    }

    @Test
    void endOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.ANSWERED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/end", token, null, status().isOk());
        assertEquals(OrderState.CHAT_ENDED, query(id).getState());
    }

    @Test
    void endInvalidOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/end", token, null, status().isForbidden());
    }

    @Test
    void cancelOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/cancel", token, null, status().isOk());
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.PAYED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/cancel", token, null, status().isOk());
        request.setState(OrderState.REVIEWED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/cancel", token, null, status().isOk());
        assertEquals(OrderState.CANCELLED, query(id).getState());
    }

    @Test
    void cancelInvalidOrder() throws Exception {
        long id = createOrder();
        mockUtils.postUrl("/api/orders/" + id + "/cancel", null, null, status().isUnauthorized());
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.ACCEPTED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/cancel", token, null, status().isForbidden());
    }

    @Test
    void queryOrderList() throws Exception {
        createOrder();
        mockUtils.getUrl("/api/orders?asker=" + askerId, token, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?asker=" + Long.MAX_VALUE, token, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders?answerer=" + askerId, token, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?answerer=" + Long.MAX_VALUE, token, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders", token, null, null, status().isForbidden());
        mockUtils.getUrl("/api/orders", null, null, null, status().isUnauthorized());
    }

    OrderResponse query(long id) throws Exception {
        MvcResult queryResult = mockUtils.getUrl("/api/orders/" + id, token, null, null, status().isOk());
        OrderResponse result = mapper.readValue(queryResult.getResponse().getContentAsString(), OrderResponse.class);
        assertEquals(result.getId(), id);
        return result;
    }

    void edit(long id, OrderRequest data) throws Exception {
        mockUtils.putUrl("/api/orders/" + id, token, data, status().isOk());
    }
}
