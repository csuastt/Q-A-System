package com.example.qa.test;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ChangePasswordRequest;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserController;
import com.example.qa.user.UserService;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.Gender;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.MockUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    private static MockUtils mockUtils;

    private String token;
    private String username;
    private long id;
    private static int userCounter = 0;
    private static final String password = "password";
    private static final String email = "example@example.com";
    private static final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private RegisterRequest newRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser" + userCounter++);
        registerRequest.setPassword(password);
        registerRequest.setEmail(email);
        return registerRequest;
    }

    @BeforeAll
    static void initiate(@Autowired MockMvc mockMvc){
        mockUtils = new MockUtils(mockMvc, mapper);
    }

    @Test
    void createUser() throws Exception {
        RegisterRequest request = newRegisterRequest();
        mockUtils.postUrl("/api/users",null, request, status().isOk());
        mockUtils.postUrl("/api/users", null, request, status().isForbidden());
    }

    @BeforeEach
    @Test
    void login() throws Exception {
        RegisterRequest registerRequest = newRegisterRequest();
        mockUtils.postUrl("/api/users", null, registerRequest, status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(password);
        TokenResponse result = mockUtils.postAndDeserialize("/api/user/login",null, loginRequest, status().isOk(), TokenResponse.class);
        assertNotNull(result.getToken(), "token 不为空");
        token = result.getToken();
        username = registerRequest.getUsername();
        id = Long.parseLong(Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject());

        loginRequest.setPassword("");
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isForbidden());
        loginRequest.setPassword(null);
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isBadRequest());
        loginRequest.setUsername("Random");
        loginRequest.setPassword(password);
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isForbidden());
        loginRequest.setUsername(null);
        mockUtils.postUrl("/api/user/login",null, loginRequest, status().isBadRequest());
    }

    @Test
    void listUsers() throws Exception {
        mockUtils.getUrl("/api/users", null, new HashMap<>() {
            {
                put("role", "ANSWERER");
            }
        }, null, status().isOk());
    }

    @Test
    void getUser() throws Exception {
        mockUtils.getUrl("/api/users/" + id, token, null, null, status().isOk());
        mockUtils.getUrl("/api/users/" + userCounter + 10, token, null, null, status().isNotFound());
        mockUtils.getUrl("/api/users/" + 1, null, null, null, status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        mockUtils.deleteUrl("/api/users/" + id, token, null, status().isForbidden());
    }

    @Test
    void editUser() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setNickname("myNickname");
        mockUtils.putUrl("/api/users/" + id, token, userRequest, status().isOk());

        userRequest.setNickname(null);
        userRequest.setPhone("example");
        userRequest.setGender(Gender.MALE);
        userRequest.setPrice(50);
        userRequest.setDescription("MyDescription");
        userRequest.setEmail(email);
        userRequest.setRole(UserRole.ANSWERER);
        userRequest.setBalance(200);
        mockUtils.putUrl("/api/users/" + id, token, userRequest, status().isOk());

        userRequest.setNickname("");
        mockUtils.putUrl("/api/users/" + id, token, userRequest, status().isOk());
    }

    @Test
    void changePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOriginal(password);
        request.setPassword(password);
        mockUtils.putUrl("/api/users/" + id + "/password", token, request, status().isOk());
    }

    @Test
    void apply() throws Exception {
        ApplyRequest request = new ApplyRequest();
        request.setDescription("MyDescription");
        request.setPrice(null);
        mockUtils.postUrl("/api/users/" + id + "/apply", token, request, status().isForbidden());
        request.setPrice(101);
        mockUtils.postUrl("/api/users/" + id + "/apply", token, request, status().isForbidden());
        request.setPrice(-1);
        mockUtils.postUrl("/api/users/" + id + "/apply", token, request, status().isForbidden());
        request.setPrice(50);
        mockUtils.postUrl("/api/users/" + id + "/apply", token, request, status().isOk());
    }

    @Test
    void recharge() throws Exception {
        ValueRequest request = new ValueRequest();
        request.setValue(50);
        mockUtils.postUrl("/api/users/" + id + "/recharge", token, request, status().isOk());
    }

    @Test
    void logOut()throws Exception{
        mockUtils.postUrl("/api/user/logout", null, null, status().isOk());
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
        userController.checkUserData(userRequest);
        userRequest.setNickname("nickname");
        userRequest.setDescription("");
        userController.checkUserData(userRequest);
        userRequest.setDescription("myDescription");
        userRequest.setPrice(-1);
        assertThrows(ApiException.class, () -> userController.checkUserData(userRequest));
        userRequest.setPrice(1);
        userRequest.setNickname("12345678910111213115161718192021222323242527282930");
        assertThrows(ApiException.class, () -> userController.checkUserData(userRequest));

        ApplyRequest applyRequest = new ApplyRequest();
        assertThrows(ApiException.class, () -> userController.checkUserData(applyRequest));
        applyRequest.setDescription("myDescription");
        assertThrows(ApiException.class, () -> userController.checkUserData(applyRequest));

        assertThrows(ApiException.class, () -> userController.checkRecharge(0, -1));
        assertThrows(ApiException.class, () -> userController.checkRecharge(10000000, 1));
    }
}
