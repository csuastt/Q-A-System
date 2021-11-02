package com.example.qa.test;

import com.example.qa.user.exchange.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        //Test for earning response
        int total = 1;
        MonthlyEarnings[] monthlyEarningsList = new MonthlyEarnings[]{};
        MonthlyEarningsResponse[] monthly = Arrays.stream(monthlyEarningsList).toList().stream().map(MonthlyEarningsResponse::new).toArray(MonthlyEarningsResponse[]::new);
        EarningsResponse earningsResponse = new EarningsResponse();
        EarningsResponse earningsResponse1 = new EarningsResponse(total, Arrays.stream(monthlyEarningsList).toList());
        earningsResponse.setTotal(total);
        earningsResponse.setMonthly(monthly);
        assertEquals(total, earningsResponse.getTotal());
        assertEquals(monthly, earningsResponse.getMonthly());
        assertEquals(total, earningsResponse1.getTotal());
    }
}
