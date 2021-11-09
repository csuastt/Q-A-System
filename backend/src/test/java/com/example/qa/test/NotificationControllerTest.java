package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.MockUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

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


    @BeforeAll
    static void addUsers(@Autowired MockMvc mockMvc,
                         @Autowired UserRepository userRepository,
                         @Autowired AdminRepository adminRepository,
                         @Autowired PasswordEncoder passwordEncoder) throws Exception {
        mockUtils = new MockUtils(mockMvc);

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("testAsker4");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();

        registerRequest.setUsername("testAnswerer4");
        User answerer = new User(registerRequest);
        answerer.setRole(UserRole.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin4");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(AdminRole.ADMIN);
        Admin admin = new Admin(adminRequest);
        adminRepository.save(admin);

        LoginRequest userRequest = new LoginRequest();
        userRequest.setUsername("testAsker4");
        userRequest.setPassword(password);
        askerToken = mockUtils.postAndDeserialize("/api/user/login", null, userRequest, status().isOk(), TokenResponse.class).getToken();

        userRequest.setUsername("testAnswerer4");
        userRequest.setPassword(password);
        answererToken = mockUtils.postAndDeserialize("/api/user/login", null, userRequest, status().isOk(), TokenResponse.class).getToken();


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();


    }

    @Test
    void getNotifications() throws Exception {
//        Optional<Boolean> hasRead = Optional.of(true);
        Boolean hasRead = true;
        int page = 1;
        int pageSize = 20;
        mockUtils.getUrl("/api/users/" + askerId + "/notif", askerToken, new HashMap<>() {
            {
                put("hasRead", String.valueOf(hasRead));
                put("page", String.valueOf(page));
                put("pageSize", String.valueOf(pageSize));
            }
        }, null, status().isOk());
    }

    @Test
    void setRead() {
    }

    @Test
    void readAll() {
    }

    @Test
    void deleteRead() {
    }
}