package com.example.qa;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserController;
import com.example.qa.user.UserService;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.Gender;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private UserService userService;

    private String token;
    private String username;
    private long id;
    private static int userCounter = 0;
    private static final String password = "password";
    private static final String email = "example@example.com";
    private static final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

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
        registerRequest.setUsername("testUser" + userCounter++);
        registerRequest.setPassword(password);
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
        TokenResponse result = postAndDeserialize("/api/user/login", loginRequest, status().isOk(), TokenResponse.class);
        assertNotNull(result.getToken(), "token 不为空");
        token = result.getToken();
        username = registerRequest.getUsername();
        id = 0; // TODO: result.getUser().getId();

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
        mockMvc.perform(get("/api/users/" + 1))
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
    void editUser() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setNickname("myNickname");
        mockMvc.perform(put("/api/users/" + id)
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        userRequest.setNickname(null);
        userRequest.setPhone("example");
        userRequest.setGender(Gender.MALE);
        userRequest.setPrice(50);
        userRequest.setDescription("MyDescription");
        userRequest.setEmail(email);
        userRequest.setRole(UserRole.ANSWERER);
        userRequest.setBalance(200);
        mockMvc.perform(put("/api/users/" + id)
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOriginal(password);
        request.setPassword(password);
        mockMvc.perform(put("/api/users/" + id + "/password")
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void apply() throws Exception {
        ApplyRequest request = new ApplyRequest();
        request.setDescription("MyDescription");
        request.setPrice(50);
        mockMvc.perform(post("/api/users/" + id + "/apply")
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void recharge() throws Exception {
        ValueRequest request = new ValueRequest();
        request.setValue(50);
        mockMvc.perform(post("/api/users/" + id + "/recharge")
                        .header(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void userModel() {
        User user = new User();
        user.getAuthorities();
        user.isAccountNonExpired();
        user.isAccountNonLocked();
        user.isEnabled();
        user.isCredentialsNonExpired();
        user.setDeleted(true);
        user.getAuthorities();
        user.isAccountNonExpired();
        user.isAccountNonLocked();
        user.isEnabled();
        user.isCredentialsNonExpired();
    }

    @Test
    void userValidators() {
        UserController userController = new UserController(userService, passwordEncoder);
        userController.validatePassword(password);
        assertThrows(ApiException.class, () -> userController.validatePassword(""));
        assertThrows(ApiException.class, () -> userController.validatePassword("passwordTooLongPasswordTooLong"));

        RegisterRequest registerRequest = newRegisterRequest();
        registerRequest.setUsername("");
        assertThrows(ApiException.class, () -> userController.checkUserData(registerRequest));
        registerRequest.setUsername("usernameTooLongUsernameTooLong");
        assertThrows(ApiException.class, () -> userController.checkUserData(registerRequest));
        registerRequest.setUsername("@Username");
        assertThrows(ApiException.class, () -> userController.checkUserData(registerRequest));
        registerRequest.setUsername("testUser");
        registerRequest.setEmail("email");
        assertThrows(ApiException.class, () -> userController.checkUserData(registerRequest));

        UserRequest userRequest = new UserRequest();
        userRequest.setPrice(50);
        userController.checkUserData(userRequest);
        userRequest.setNickname("");
        assertThrows(ApiException.class, () -> userController.checkUserData(userRequest));
        userRequest.setNickname("nickname");
        userRequest.setDescription("");
        assertThrows(ApiException.class, () -> userController.checkUserData(userRequest));
        userRequest.setDescription("myDescription");
        userRequest.setPrice(-1);
        assertThrows(ApiException.class, () -> userController.checkUserData(userRequest));

        ApplyRequest applyRequest = new ApplyRequest();
        assertThrows(ApiException.class, () -> userController.checkUserData(applyRequest));
        applyRequest.setDescription("myDescription");
        assertThrows(ApiException.class, () -> userController.checkUserData(applyRequest));

        assertThrows(ApiException.class, () -> userController.checkRecharge(0, -1));
        assertThrows(ApiException.class, () -> userController.checkRecharge(10000000, 1));
    }
}
