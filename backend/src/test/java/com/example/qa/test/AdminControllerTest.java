package com.example.qa.test;

import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.utils.MockUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    private static MockUtils mockUtils;

    private static String superAdminToken;
    private String username;
    private String password;
    private long id;
    private static int adminCounter = 0;

    @BeforeAll
    // @Test
    static void loginSuperAdmin(@Autowired MockMvc mockMvc, @Autowired JsonMapper mapper) throws Exception {
        mockUtils = new MockUtils(mockMvc, mapper);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();
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
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isOk());
        request.setUsername("testAdmin" + adminCounter++);
        request.setRole(AdminRole.ADMIN);
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isOk());
        request.setRole(AdminRole.SUPER_ADMIN);
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isForbidden());
    }

    @Test
    void listAdmins() throws Exception {
        mockUtils.getUrl("/api/admins", superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/admins", null, null, null, status().isUnauthorized());
    }

    @Test
    void getAdmin() throws Exception {
        mockUtils.getUrl("/api/admins/" + 1, superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/admins/" + Long.MAX_VALUE, superAdminToken, null, null, status().isNotFound());
    }

    @Test
    void logOut() throws Exception {
        mockUtils.postUrl("/api/admin/logout", null, null, status().isOk());
    }

    @Test
    void getUser() throws Exception {
        mockUtils.getUrl("/api/users/" + 1, superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/users/" + 1, null, null, null, status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        mockUtils.deleteUrl("/api/users/" + 1, superAdminToken, null, status().isOk());
        mockUtils.getUrl("/api/users/" + 1, superAdminToken, null, null, status().isOk());
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("useruser");
        registerRequest.setPassword("password");
        registerRequest.setEmail("177@qq.com");
        mockUtils.postUrl("/api/users", null, registerRequest, status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword("password");
        TokenResponse result = mockUtils.postAndDeserialize("/api/user/login", null, loginRequest, status().isOk(), TokenResponse.class);
        mockUtils.getUrl("/api/users/" + 1, result.getToken(), null, null, status().isNotFound());
    }

    @Test
    void getOrderList() throws Exception {
        mockUtils.getUrl("/api/orders?state=REVIEWED", superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders?state=REVIEWED", null, null, null, status().isUnauthorized());
        mockUtils.getUrl("/api/orders", superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders", null, null, null, status().isUnauthorized());
    }
}
