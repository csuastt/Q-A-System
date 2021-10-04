package com.example.qa.user;

import com.example.qa.user.repository.UserRepository;
import com.example.qa.user.response.GetAllResponse;
import com.example.qa.user.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static com.example.qa.user.JsonHelper.fromJson;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;
    @Autowired
    private UserRepository repository;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    @Test
    void setUp() throws Exception {
        MvcResult loginResult = this.mockMvc
                .perform(post("/api/user/login?username=testUser&password=password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = fromJson(objectMapper, loginResult.getResponse().getContentAsString(), LoginResponse.class);
        this.token = response.getToken();
        assertNotNull(response.getToken(), "Token must not be null!");
        assertEquals(response.getUser().getUsername(), "testUser");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllUsers() throws Exception {
        MvcResult getAllResult = this.mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse response = fromJson(objectMapper, getAllResult.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(response.getUsers().size(), repository.count());
    }

    @Test
    void getUser() {
    }

    @Test
    void permitQuest() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void genAvatar() {
    }

    @Test
    void register() {
    }

    @Test
    void modifyUser() {
    }

    @Test
    void modifyPass() {
    }
}