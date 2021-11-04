package com.example.qa.test;

import com.example.qa.admin.exchange.AdminListResponse;
import com.example.qa.admin.exchange.AdminResponse;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.config.Configurable;
import com.example.qa.config.EarningsResponse;
import com.example.qa.errorhandling.ApiError;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminBeanTest {

    @Test
    void testEquals_Symmetric(){

        //Test for UserAuthentication
        UserAuthentication user1 = new UserAuthentication(1L, User.class);
        UserAuthentication user2 = new UserAuthentication(1L, User.class);
        UserAuthentication user4 = new UserAuthentication(1L, Admin.class);
        UserAuthentication user5 = new UserAuthentication(2L, User.class);
        UserAuthentication user3 = null;
        User user = new User();
        assertFalse(user1.equals(user3) || user1.equals(user));
        assertFalse(user1.equals(user4) || user1.equals(user5));
        assertTrue(user1.equals(user2) && user2.equals(user1));
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testSetProperty(){

        //Test for AdminListResponse
        int adminPageSize = 1;
        int adminPage = 1;
        int totalPages = 2;
        int totalCount = 4;
        AdminResponse[] data = new AdminResponse[]{};
        AdminListResponse adminListResponse = new AdminListResponse();
        adminListResponse.setPageSize(adminPageSize);
        adminListResponse.setPage(adminPage);
        adminListResponse.setTotalPages(totalPages);
        adminListResponse.setTotalCount(totalCount);
        adminListResponse.setData(data);
        assertEquals(adminPageSize, adminListResponse.getPageSize());
        assertEquals(adminPage, adminListResponse.getPage());
        assertEquals(totalPages, adminListResponse.getTotalPages());
        assertEquals(totalCount, adminListResponse.getTotalCount());
        assertEquals(data, adminListResponse.getData());

        //Test for AdminResponse
        Long adminId = 1L;
        Boolean adminDeleted = false;
        String adminUsername = "test";
        ZonedDateTime adminCreateTime = ZonedDateTime.now();
        AdminRole adminRole = AdminRole.SUPER_ADMIN;
        AdminResponse adminResponse = new AdminResponse();
        adminResponse.setId(adminId);
        adminResponse.setDeleted(adminDeleted);
        adminResponse.setUsername(adminUsername);
        adminResponse.setCreateTime(adminCreateTime);
        adminResponse.setRole(adminRole);
        assertEquals(adminId, adminResponse.getId());
        assertEquals(adminDeleted, adminResponse.getDeleted());
        assertEquals(adminUsername, adminResponse.getUsername());
        assertEquals(adminCreateTime, adminResponse.getCreateTime());
        assertEquals(adminRole, adminResponse.getRole());

        //Test for PasswordResponse
        String password = "123456";
        PasswordResponse response = new PasswordResponse();
        response.setPassword(password);
        assertEquals(password, response.getPassword());

        //Test for Admin
        long Id = 1L;
        boolean Deleted = false;
        String Username = "test";
        ZonedDateTime CreateTime = ZonedDateTime.now();
        AdminRole Role = AdminRole.SUPER_ADMIN;
        Admin admin = new Admin();
        admin.setId(Id);
        admin.setDeleted(Deleted);
        admin.setUsername(Username);
        admin.setCreateTime(CreateTime);
        admin.setRole(Role);

        assertEquals(Id, admin.getId());
        assertEquals(Username, admin.getUsername());
        assertEquals(CreateTime, admin.getCreateTime());
        assertEquals(Role, admin.getRole());


        //Test for ApiError
        String message = "new";
        ApiError error = new ApiError("");
        error.setMessage(message);
        assertEquals(message, error.getMessage());

        //Test for Configurable
        Integer minPrice = 1;
        Integer maxPrice = 10;
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
        assertEquals(maxPrice, configurable.getMaxPrice());
        assertEquals(minPrice, configurable.getMinPrice());
        assertEquals(respondExpirationSeconds, configurable.getRespondExpirationSeconds());
        assertEquals(answerExpirationSeconds, configurable.getAnswerExpirationSeconds());
        assertEquals(fulfillExpirationSeconds, configurable.getFulfillExpirationSeconds());
        assertEquals(maxChatMessages, configurable.getMaxChatMessages());
        assertEquals(maxChatTimeSeconds, configurable.getMaxChatTimeSeconds());
        assertEquals(feeRate, configurable.getFeeRate());

        //Test for EarningsResponse
        int earn = 1;
        EarningsResponse response1 = new EarningsResponse(earn);
        assertEquals(earn, response1.getEarnings());
        earn = 2;
        response1.setEarnings(earn);
        assertEquals(earn, response1.getEarnings());
    }
}
