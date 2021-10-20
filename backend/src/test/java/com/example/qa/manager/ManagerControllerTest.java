package com.example.qa.manager;

import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.AuthenticationSuccessResponse;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private String token;
    private String password = "password";
    @Autowired
    private UserRepository repository;

    private final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();


    @BeforeEach
    @Test
    void login() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("testUser");
        register.setPassword("password");

        //test register success
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(register)))
                .andReturn();

        //test for login success
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword(password);
        MvcResult loginResult = this.mockMvc
                .perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationSuccessResponse response = mapper.readValue(loginResult.getResponse().getContentAsString(), AuthenticationSuccessResponse.class);
        this.token = response.getToken();
        assertNotNull(response.getToken(), "Token must not be null!");
        assertEquals(response.getUser().getUsername(), "testUser");

        loginRequest.setPassword("pa");

        //test for wrong password login
        this.mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
        //test for wrong username
        loginRequest.setUsername("t");
        this.mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void getManagers() throws Exception {
        this.mockMvc.perform(get("/api/managers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getManager() throws Exception {
        this.mockMvc.perform(get("/api/managers/1")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
    }

    @Test
    void permitQuest() throws Exception {
        this.mockMvc.perform(get("/api/managers/1/permission")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void deleteUser() throws Exception {
        this.mockMvc.perform(delete("/api/managers")
                        .param("id", "1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void register() throws Exception {
        this.mockMvc.perform(post("/api/managers")
                        .param("managername", "test")
                        .param("password", "password")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
    }

    @Test
    void modifyUser() throws Exception {
        this.mockMvc.perform(put("/api/managers/1")
                        .param("managername", "test")
                        .param("password", "password")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
    }

    @Test
    void modifyPass() throws Exception {
        this.mockMvc.perform(put("/api/managers/1/password")
                        .param("managername", "test")
                        .param("password", "password")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
    }
}