package com.example.qa.test;

import com.example.qa.admin.exchange.AdminListResponse;
import com.example.qa.admin.exchange.AdminResponse;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.model.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;

@SpringBootTest
class AdminBeanTest {

    @Test
    void testEquals_Symmetric(){

        //Test for UserAuthentication
        UserAuthentication user1 = new UserAuthentication(1L, User.class);
        UserAuthentication user2 = new UserAuthentication(1L, User.class);
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
        assertEquals(adminId, admin.getId());
        assertEquals(adminUsername, admin.getUsername());
        assertEquals(adminCreateTime, admin.getCreateTime());
        assertEquals(adminRole, admin.getRole());
    }
}
