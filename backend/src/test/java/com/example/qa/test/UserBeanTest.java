package com.example.qa.test;

import com.example.qa.user.exchange.UserListResponse;
import com.example.qa.user.exchange.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserBeanTest {

    @Test
    void testSetProperty(){

        //Test for UserListResponse
        int pageSize = 1;
        int page = 1;
        int totalPages = 1;
        long totalCount = 1;
        UserResponse[] data = new UserResponse[]{};
        UserListResponse response = new UserListResponse();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalPages(totalPages);
        response.setTotalCount(totalCount);
        response.setData(data);
        assertEquals(page, response.getPage());
        assertEquals(pageSize, response.getPageSize());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(totalCount, response.getTotalCount());
        assertEquals(data, response.getData());
    }
}
