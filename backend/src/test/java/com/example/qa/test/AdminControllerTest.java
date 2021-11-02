package com.example.qa.test;

import com.example.qa.admin.AdminService;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.security.SecurityConstants;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.utils.MockUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    private final MockUtils mockUtils;

    private String token;
    private String username;
    private String password;
    private long id;
    private static int adminCounter = 0;
    private static final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Autowired
    public AdminControllerTest(MockMvc mockMvc, PasswordEncoder passwordEncoder, AdminService adminService) {
        mockUtils = new MockUtils(mockMvc, mapper);
    }

    @BeforeEach
    @Test
    void loginSuperAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        token = tokenResponse.getToken();
    }

    @Test
    void loginFailure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        mockUtils.postUrl("/api/admin/login", null, loginRequest, status().isBadRequest());
        loginRequest.setUsername(null);
        mockUtils.postUrl("/api/admin/login", null, loginRequest, status().isBadRequest());
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME + "@");
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD + "@");
        mockUtils.postUrl("/api/admin/login", null, loginRequest, status().isForbidden());
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        mockUtils.postUrl("/api/admin/login", null, loginRequest, status().isForbidden());
    }

    @Test
    void createAdmin() throws Exception {
        AdminRequest request = new AdminRequest();
        request.setUsername("testAdmin" + adminCounter++);
//        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", token, request, status().isOk());
        request.setUsername("testAdmin" + adminCounter++);
        request.setRole(AdminRole.ADMIN);
        //        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", token, request, status().isOk());
        request.setRole(AdminRole.SUPER_ADMIN);
        //        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", token, request, status().isForbidden());
    }

    @Test
    void listAdmins() throws Exception {
        mockUtils.getUrl("/api/admins", token, null, null, status().isOk());
        mockUtils.getUrl("/api/admins", null, null, null, status().isUnauthorized());
    }

    @Test
    void getAdmin() throws Exception {
        mockUtils.getUrl("/api/admins/" + 1, token, null, null, status().isOk());
        mockUtils.getUrl("/api/admins/" + Long.MAX_VALUE, token, null, null, status().isNotFound());
    }

    @Test
    void logOut() throws Exception{
        mockUtils.postUrl("/api/admin/logout", null, null, status().isOk());
    }

    @Test
    void getUser() throws Exception{
        mockUtils.getUrl("/api/users/" + 1, token, null, null, status().isOk());
        mockUtils.getUrl("/api/users/" + 1, null, null, null, status().isOk());
    }
}
