package com.example.qa.test;

import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.ChangePasswordRequest;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.UserRequest;
import com.example.qa.user.model.User;
import com.example.qa.utils.MockUtils;
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
    private static int adminCounter = 10;

    @BeforeAll
    // @Test
    static void loginSuperAdmin(@Autowired MockMvc mockMvc) throws Exception {
        mockUtils = new MockUtils(mockMvc);
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
        request.setRole(Admin.Role.ADMIN);
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isOk());
        request.setRole(Admin.Role.SUPER_ADMIN);
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isForbidden());
        request.setUsername("@testUser" + adminCounter++);
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isForbidden());
        request.setUsername("testUser" + adminCounter);
        request.setRole(Admin.Role.REVIEWER);
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isOk());
        request.setUsername("testUser" + adminCounter);
        request.setRole(Admin.Role.REVIEWER);
        mockUtils.postUrl("/api/admins", superAdminToken, request, status().isForbidden());
    }

    @Test
    void listAdmins() throws Exception {
        mockUtils.getUrl("/api/admins", superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/admins", null, null, null, status().isUnauthorized());
    }

    @Test
    void editAdmin()throws Exception{
        createAdmin();
        long id = 2;
        String username = "testAdminUser";
        String password = "passW";
        Admin.Role role = Admin.Role.REVIEWER;
        AdminRequest request = new AdminRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRole(role);
        mockUtils.putUrl("/api/admins/" + id, superAdminToken ,request, status().isOk());
        mockUtils.putUrl("/api/admins/" + 1, superAdminToken ,request, status().isForbidden());
        request.setRole(Admin.Role.SUPER_ADMIN);
        mockUtils.putUrl("/api/admins/" + id, superAdminToken ,request, status().isForbidden());

        ChangePasswordRequest request1 = new ChangePasswordRequest();
        request1.setPassword("passWW");
        request1.setOriginal("passW");
        mockUtils.putUrl("/api/admins/" + id + "/password", superAdminToken ,request1, status().isOk());
        mockUtils.putUrl("/api/admins/" + id + "/password", superAdminToken ,request1, status().isOk());
        mockUtils.putUrl("/api/admins/" + id + "/password", null ,request1, status().isUnauthorized());

    }

    @Test
    void deleteAdmin() throws Exception {
        createAdmin();
        mockUtils.deleteUrl("/api/admins/" + 2, superAdminToken, null, status().isOk());
        mockUtils.deleteUrl("/api/admins/" + 2, superAdminToken, null, status().isForbidden());
        mockUtils.deleteUrl("/api/admins/" + 2, null, null, status().isUnauthorized());
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
        UserRequest userRequest = new UserRequest();
        userRequest.setNickname("myNickname");
        mockUtils.putUrl("/api/users/" + 1, superAdminToken, userRequest, status().isOk());

        userRequest.setNickname(null);
        userRequest.setPhone("example");
        userRequest.setGender(User.Gender.MALE);
        userRequest.setPrice(50);
        userRequest.setDescription("MyDescription");
        userRequest.setEmail("177@qq.com");
        userRequest.setRole(User.Role.ANSWERER);
        userRequest.setBalance(200);
        mockUtils.putUrl("/api/users/" + 1, superAdminToken, userRequest, status().isOk());

        userRequest.setNickname("");
        mockUtils.putUrl("/api/users/" + 1, superAdminToken, userRequest, status().isOk());
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
