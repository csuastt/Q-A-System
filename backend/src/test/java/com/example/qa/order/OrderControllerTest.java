package com.example.qa.order;

import com.example.qa.order.exchange.AcceptData;
import com.example.qa.order.exchange.OrderData;
import com.example.qa.order.exchange.OrderEditData;
import com.example.qa.order.model.OrderState;
import com.example.qa.order.repository.OrderRepository;
import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
import com.example.qa.user.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collection;

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
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void addUsers(@Autowired UserRepository userRepository, @Autowired PasswordEncoder passwordEncoder) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        AppUser asker = new AppUser("testAsker", passwordEncoder.encode(password), authorities);
        asker.setPermit("q");
        userRepository.save(asker);
        AppUser answerer = new AppUser("testAnswerer", passwordEncoder.encode(password), authorities);
        answerer.setPermit("a");
        userRepository.save(answerer);
    }

    @BeforeEach
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testAsker");
        loginRequest.setPassword(password);
        MvcResult loginResult = this.mockMvc
                .perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        LoginResponse response = new Gson().fromJson(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        this.token = response.getToken();

        mapper.findAndRegisterModules();
    }

    @Test
    long createOrder() throws Exception {
        OrderEditData request = new OrderEditData();
        request.setAsker(1L);
        request.setAnswerer(2L);
        request.setQuestion(question);
        MvcResult createResult = mockMvc
                .perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        OrderData result = mapper.readValue(createResult.getResponse().getContentAsString(), OrderData.class);
        assertEquals(result.getAsker().id, 1L);
        assertEquals(result.getAnswerer().id, 2L);
        assertEquals(result.getQuestion(), question);
        assertEquals(result.getState(), OrderState.CREATED);
        return result.getId();
    }

    @Test
    void createOrderWithInvalidAsker() throws Exception {
        OrderEditData request = new OrderEditData();
        request.setAsker(99L);
        request.setAnswerer(2L);
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
    void createOrderWithInvalidAnswerer() throws Exception {
        OrderEditData request = new OrderEditData();
        request.setAsker(1L);
        request.setAnswerer(1L);
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
        OrderEditData request = new OrderEditData();
        request.setAsker(1L);
        request.setAnswerer(2L);
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
        OrderEditData request = new OrderEditData();
        request.setQuestion(newQuestion);
        edit(id, request);
        assertEquals(query(id).getQuestion(), newQuestion);
    }

    @Test
    void reviewOrder() throws Exception {
        long id = createOrder();
        OrderEditData request = new OrderEditData();
        request.setState(OrderState.PAYED);
        edit(id, request);
        AcceptData accept = new AcceptData(true);
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
        AcceptData accept = new AcceptData(true);
        mockMvc.perform(post("/api/orders/" + id + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isForbidden());
    }

    @Test
    void respondOrder() throws Exception {
        long id = createOrder();
        OrderEditData request = new OrderEditData();
        request.setState(OrderState.REVIEWED);
        edit(id, request);
        AcceptData accept = new AcceptData(true);
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
        AcceptData accept = new AcceptData(true);
        mockMvc.perform(post("/api/orders/" + id + "/respond")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isForbidden());
    }

    @Test
    void endOrder() throws Exception {
        long id = createOrder();
        OrderEditData request = new OrderEditData();
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


    OrderData query(long id) throws Exception {
        MvcResult queryResult = mockMvc
                .perform(get("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        OrderData result = mapper.readValue(queryResult.getResponse().getContentAsString(), OrderData.class);
        assertEquals(result.getId(), id);
        return result;
    }

    void edit(long id, OrderEditData data) throws Exception {
        mockMvc.perform(put("/api/orders/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk());
    }
}
