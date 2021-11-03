package com.example.qa.test;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ChangePasswordRequest;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.exchange.ApplyRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.UserRequest;
import com.example.qa.user.exchange.ValueRequest;
import com.example.qa.user.model.Gender;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.MockUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static MockUtils mockUtils;

    private static String token;
    private static long id;
    private static int userCounter = 0;
    private static String username;
    private static final String password = "password";
    private static final String email = "example@example.com";

    @BeforeAll
    // @Test
    static void login(@Autowired MockMvc mockMvc, @Autowired JsonMapper mapper) throws Exception {
        mockUtils = new MockUtils(mockMvc, mapper);

        RegisterRequest registerRequest = newRegisterRequest();
        mockUtils.postUrl("/api/users", null, registerRequest, status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(password);
        TokenResponse result = mockUtils.postAndDeserialize("/api/user/login", null, loginRequest, status().isOk(), TokenResponse.class);
        assertNotNull(result.getToken(), "token 不为空");
        token = result.getToken();
        username = registerRequest.getUsername();
        id = Long.parseLong(Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject());
    }

    private static RegisterRequest newRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser" + userCounter++);
        registerRequest.setPassword(password);
        registerRequest.setEmail(email);
        return registerRequest;
    }

    @Test
    void createUser() throws Exception {
        RegisterRequest request = newRegisterRequest();
        mockUtils.postUrl("/api/users", null, request, status().isOk());
        mockUtils.postUrl("/api/users", null, request, status().isForbidden());
    }

    @BeforeEach
    @Test
    void loginFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword("");
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isForbidden());
        loginRequest.setPassword(null);
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isBadRequest());
        loginRequest.setUsername("Random");
        loginRequest.setPassword(password);
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isForbidden());
        loginRequest.setUsername(null);
        mockUtils.postUrl("/api/user/login", null, loginRequest, status().isBadRequest());
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
    void logOut() throws Exception {
        mockUtils.postUrl("/api/user/logout", null, null, status().isOk());
    }

    @Test
    void userValidators() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setPassword(password);
        changePasswordRequest.validatePasswordOrThrow();
        changePasswordRequest.setPassword("");
        assertThrows(ApiException.class, changePasswordRequest::validatePasswordOrThrow);
        changePasswordRequest.setPassword("passwordTooLongPasswordTooLong");
        assertThrows(ApiException.class, changePasswordRequest::validatePasswordOrThrow);

        RegisterRequest registerRequest = newRegisterRequest();
        registerRequest.setUsername("");
        assertThrows(ApiException.class, registerRequest::validateOrThrow);
        registerRequest.setUsername("usernameTooLongUsernameTooLong");
        assertThrows(ApiException.class, registerRequest::validateOrThrow);
        registerRequest.setUsername("@Username");
        assertThrows(ApiException.class, registerRequest::validateOrThrow);
        registerRequest.setUsername("testUser");
        registerRequest.setEmail("email");
        assertThrows(ApiException.class, registerRequest::validateOrThrow);

        UserRequest userRequest = new UserRequest();
        userRequest.setPrice(50);
        userRequest.validateOrThrow();
        userRequest.setNickname("");
        userRequest.validateOrThrow();
        userRequest.setNickname("nickname");
        userRequest.setDescription("");
        userRequest.validateOrThrow();
        userRequest.setDescription("myDescription");
        userRequest.setPrice(-1);
        assertThrows(ApiException.class, userRequest::validateOrThrow);
        userRequest.setPrice(1);
        userRequest.setNickname("12345678910111213115161718192021222323242527282930");
        assertThrows(ApiException.class, userRequest::validateOrThrow);

        ApplyRequest applyRequest = new ApplyRequest();
        assertThrows(ApiException.class, applyRequest::validateOrThrow);
        applyRequest.setDescription("myDescription");
        assertThrows(ApiException.class, applyRequest::validateOrThrow);

        ValueRequest valueRequest = new ValueRequest();
        valueRequest.setValue(-1);
        assertThrows(ApiException.class, () -> valueRequest.checkRechargeOrThrow(0));
        valueRequest.setValue(1);
        assertThrows(ApiException.class, () -> valueRequest.checkRechargeOrThrow(1000000000));
    }
}
