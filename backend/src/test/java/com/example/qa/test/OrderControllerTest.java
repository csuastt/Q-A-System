package com.example.qa.test;

import com.example.qa.order.OrderRepository;
import com.example.qa.order.exchange.AcceptRequest;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.TokenResponse;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    private String token;
    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private static long askerId;
    private static long answererId;

    @BeforeAll
    static void addUsers(@Autowired UserRepository userRepository, @Autowired PasswordEncoder passwordEncoder) {
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
        MvcResult loginResult = this.mockMvc
                .perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        TokenResponse response = mapper.readValue(loginResult.getResponse().getContentAsString(), TokenResponse.class);
        this.token = response.getToken();
    }

    @Test
    long createOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setQuestion(question);
        MvcResult createResult = mockMvc
                .perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        OrderResponse result = mapper.readValue(createResult.getResponse().getContentAsString(), OrderResponse.class);
        assertEquals(result.getAsker().getId(), askerId);
        assertEquals(result.getAnswerer().getId(), answererId);
        assertEquals(result.getQuestion(), question);
        assertEquals(result.getState(), OrderState.CREATED);
        return result.getId();
    }

    @Test
    void createOrderWithInvalidAskerByUserButNeedToChangeToAdmin() throws Exception {
        // 需要改成管理员登录
        OrderRequest request = new OrderRequest();
        request.setAsker(Long.MAX_VALUE);
        request.setAnswerer(answererId);
        request.setQuestion(question);
        MvcResult createResult = mockMvc
                .perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void createOrderWithInvalidAnswerer() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(askerId);
        request.setQuestion(question);
        MvcResult createResult = mockMvc
                .perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void createOrderWithInvalidQuestion() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setQuestion("q");
        MvcResult createResult = mockMvc
                .perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void deleteOrder() throws Exception {
        long id = createOrder();
        mockMvc.perform(delete("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void queryOrder() throws Exception {
        query(createOrder());
    }

    @Test
    void queryInvalidOrder() throws Exception {
        mockMvc.perform(get("/api/orders/" + -1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
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
        mockMvc.perform(post("/api/orders/" + id + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isOk());
        assertEquals(query(id).getState(), OrderState.REVIEWED);
    }

    @Test
    void reviewInvalidOrder() throws Exception {
        long id = createOrder();
        AcceptRequest accept = new AcceptRequest(true);
        mockMvc.perform(post("/api/orders/" + id + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isForbidden());
    }

    @Test
    void respondOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.REVIEWED);
        edit(id, request);
        AcceptRequest accept = new AcceptRequest(true);
        mockMvc.perform(post("/api/orders/" + id + "/respond")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isOk());
        assertEquals(query(id).getState(), OrderState.ACCEPTED);
    }

    @Test
    void respondInvalidOrder() throws Exception {
        long id = createOrder();
        AcceptRequest accept = new AcceptRequest(true);
        mockMvc.perform(post("/api/orders/" + id + "/respond")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isForbidden());
    }

    @Test
    void endOrder() throws Exception {
        long id = createOrder();
        OrderRequest request = new OrderRequest();
        request.setState(OrderState.ANSWERED);
        edit(id, request);
        mockMvc.perform(post("/api/orders/" + id + "/end")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        assertEquals(query(id).getState(), OrderState.CHAT_ENDED);
    }

    @Test
    void endInvalidOrder() throws Exception {
        long id = createOrder();
        mockMvc.perform(post("/api/orders/" + id + "/end")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void queryOrderList() throws Exception {
        createOrder();
        MvcResult mvcResult = mockMvc
                .perform(get("/api/orders?asker=" + askerId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
    }

    OrderResponse query(long id) throws Exception {
        MvcResult queryResult = mockMvc
                .perform(get("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        OrderResponse result = mapper.readValue(queryResult.getResponse().getContentAsString(), OrderResponse.class);
        assertEquals(result.getId(), id);
        return result;
    }

    void edit(long id, OrderRequest data) throws Exception {
        mockMvc.perform(put("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk());
    }
}
