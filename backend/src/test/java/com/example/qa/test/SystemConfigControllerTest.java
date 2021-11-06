package com.example.qa.test;

import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.config.Configurable;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.security.SecurityConstants;
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
class SystemConfigControllerTest {

    private static MockUtils mockUtils;

    private static String superAdminToken;
    private static String adminToken;
    private String username;
    private String password;
    private long id;
    private static int adminCounter = 0;

    @BeforeAll
    // @Test
    static void loginSuperAdmin(@Autowired MockMvc mockMvc) throws Exception {
        //Test for super Admin
        mockUtils = new MockUtils(mockMvc);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();

        // Test for admin
        AdminRequest request = new AdminRequest();
        request.setUsername("testAdmin" + adminCounter);
        PasswordResponse response = mockUtils.postAndDeserialize("/api/admins", superAdminToken, request, status().isOk(), PasswordResponse.class);
        LoginRequest logRequest = new LoginRequest();
        logRequest.setUsername("testAdmin" + adminCounter);
        logRequest.setPassword(response.getPassword());
        TokenResponse token = mockUtils.postAndDeserialize("/api/admin/login", null, logRequest, status().isOk(), TokenResponse.class);
        adminToken = token.getToken();
    }

    @Test
    void getConfig() throws Exception {
        mockUtils.getUrl("/api/config", null, null, null, status().isOk());
    }

    @Test
    void updateConfig() throws Exception {
        Integer minPrice = 1;
        Integer maxPrice = 100;
        Integer respondExpirationSeconds = 7;
        Integer answerExpirationSeconds = 8;
        Integer fulfillExpirationSeconds = 6;
        Integer maxChatMessages = 3;
        Integer maxChatTimeSeconds = 2;
        Integer feeRate = 100;
        Configurable configurable = new Configurable();
        configurable.setMinPrice(minPrice);
        configurable.setMaxPrice(maxPrice);
        configurable.setRespondExpirationSeconds(respondExpirationSeconds);
        configurable.setAnswerExpirationSeconds(answerExpirationSeconds);
        configurable.setFulfillExpirationSeconds(fulfillExpirationSeconds);
        configurable.setMaxChatMessages(maxChatMessages);
        configurable.setMaxChatTimeSeconds(maxChatTimeSeconds);
        configurable.setFeeRate(feeRate);
        mockUtils.putUrl("/api/config", superAdminToken, configurable, status().isOk());
        mockUtils.putUrl("/api/config", adminToken, configurable, status().isForbidden());
        mockUtils.putUrl("/api/config", null, configurable, status().isUnauthorized());
    }

    @Test
    void earnings() throws Exception {

        mockUtils.getUrl("/api/config/earnings", superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/config/earnings", adminToken, null, null, status().isForbidden());
        mockUtils.getUrl("/api/config/earnings", null, null, null, status().isUnauthorized());

    }
}