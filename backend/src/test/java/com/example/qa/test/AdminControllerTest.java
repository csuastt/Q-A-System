package com.example.qa.test;

import com.example.qa.admin.AdminService;
import com.example.qa.admin.exchange.CreateAdminRequest;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.TokenResponse;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    private final MockMvc mockMvc;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final MockUtils mockUtils;

    private String token;
    private String username;
    private String password;
    private long id;
    private static int adminCounter = 0;
    private static final JsonMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Autowired
    public AdminControllerTest(MockMvc mockMvc, PasswordEncoder passwordEncoder, AdminService adminService) {
        this.mockMvc = mockMvc;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
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
        CreateAdminRequest request = new CreateAdminRequest();
        request.setUsername("testAdmin" + adminCounter++);
        mockUtils.postUrl("/api/admins", token, request, status().isOk());
        request.setUsername("testAdmin" + adminCounter++);
        request.setRole(AdminRole.ADMIN);
        mockUtils.postUrl("/api/admins", token, request, status().isOk());
        request.setRole(AdminRole.SUPER_ADMIN);
        mockUtils.postUrl("/api/admins", token, request, status().isForbidden());
    }

    @Test
    void listAdmins() throws Exception {
        mockMvc.perform(get("/api/admins")).andExpect(status().isOk()).andReturn();
    }

    @Test
    void getAdmin() throws Exception {
        mockMvc.perform(get("/api/admins/" + 1)).andExpect(status().isOk()).andReturn();
        mockMvc.perform(get("/api/admins/" + Long.MAX_VALUE)).andExpect(status().isNotFound()).andReturn();
    }
}
