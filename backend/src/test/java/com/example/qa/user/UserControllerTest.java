package com.example.qa.user;

import com.example.qa.security.SecurityConstants;
import com.example.qa.user.exchange.ChangePasswordRequest;
import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.UserRequest;
import com.example.qa.user.model.User;
import com.example.qa.user.response.GetAllResponse;
import com.example.qa.user.response.GetUserResponse;
import com.example.qa.user.response.LoginResponse;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;

    private String token;
    private String username;
    private long id;
    private int userCounter = 0;
    private static final String password = "password";
    private static final String email = "example@example.com";
    private final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private MvcResult postUrl(String url, Object request, ResultMatcher matcher) throws Exception {
        return mockMvc
                .perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(matcher)
                .andReturn();
    }

    private <T> T postAndDeserialize(String url, Object request, ResultMatcher matcher, Class<T> type) throws Exception {
        return mapper.readValue(postUrl(url, request, matcher).getResponse().getContentAsString(), type);
    }

    private RegisterRequest newRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser" + userCounter);
        userCounter++;
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        return registerRequest;
    }

    @Test
    void createUser() throws Exception {
        RegisterRequest request = newRegisterRequest();
        postUrl("/api/users", request, status().isOk());
        postUrl("/api/users", request, status().isForbidden());
    }

    @BeforeEach
    @Test
    void login() throws Exception {
        RegisterRequest registerRequest = newRegisterRequest();
        postUrl("/api/users", registerRequest, status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(password);
        LoginResponse result = postAndDeserialize("/api/user/login", loginRequest, status().isOk(), LoginResponse.class);
        assertNotNull(result.getToken(), "token 不为空");
        token = result.getToken();
        username = registerRequest.getUsername();
        id = result.getUser().getId();

        loginRequest.setPassword("");
        postUrl("/api/user/login", loginRequest, status().isForbidden());
        loginRequest.setPassword(null);
        postUrl("/api/user/login", loginRequest, status().isBadRequest());
        loginRequest.setUsername("Random");
        loginRequest.setPassword(password);
        postUrl("/api/user/login", loginRequest, status().isForbidden());
        loginRequest.setUsername(null);
        postUrl("/api/user/login", loginRequest, status().isBadRequest());
    }

    @Test
    void listUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("role", "ANSWERER"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getUser() throws Exception {
        mockMvc.perform(get("/api/users/" + id)
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/api/users/" + 1)
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + id)
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/users/" + id)
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void register() throws Exception {

        long expected = repository.count() + 1;

        RegisterRequest register = new RegisterRequest();
        register.setUsername("eeee");
        register.setPassword("eeee");

        //test register success
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expected, repository.count());

        //test register existed username
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(register)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void modifyUser() throws Exception {
        UserRequest modify = new UserRequest();
        modify.setUsername("eeeee");
        modify.setPassword("eeeee");
        modify.setBirthday("2000-10-03");
        modify.setDescription("A student");
        modify.setEmail("@mails.tsinghua.edu.cn");
        modify.setGender("male");
        modify.setPhone("1010");
        modify.setNickname("little");

        //test modify without permission
        this.mockMvc.perform(put("/api/users/2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //test modify without permission
        this.mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();

        if (repository.findById(1L).isEmpty()) {
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();

        assertEquals(user.getUsername(), "eeeee");
        assertEquals(user.getBirthday(), LocalDate.parse("2000-10-03"));
        assertEquals(user.getEmail(), "@mails.tsinghua.edu.cn");
        assertEquals(user.getDescription(), "A student");
        assertEquals(user.getGender(), "male");
        assertEquals(user.getPhone(), "1010");
        assertEquals(user.getNickname(), "little");

        //test modify user not existed
        long id = repository.count() + 1;
        this.mockMvc.perform(put("/api/users/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //test not authenticated
        this.mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //re-modify
        modify.setUsername("testUser");
        this.mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void modifyPass() throws Exception {
        ChangePasswordRequest modify = new ChangePasswordRequest();
        modify.setOrigin("password");
        modify.setPassword("pass");

        //test success modify password
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();
        this.password = "pass";
        login();

        //test wrong origin
        modify.setOrigin("password");
        modify.setPassword("password");
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //test not authenticated
        modify.setOrigin("pass");
        modify.setPassword("password");
        this.mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //change back to origin
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();
        this.password = "password";
    }

    /**
     * generate testUsers with permission a
     *
     * @param username name of answerers
     */
    void generateAnswerer(String username) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new User(username, passwordEncoder.encode("password"), authorities);
        user.setPermit("a");
        user.setPrice(30);
        repository.save(user);
    }

    /**
     * generate testUsers
     *
     * @param username name to create
     */
    void generateUser(String username) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new User(username, passwordEncoder.encode("password"), authorities);
        repository.save(user);
    }
}