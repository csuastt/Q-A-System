package com.example.qa.test;

import com.example.qa.exchange.EarningsResponse;
import com.example.qa.exchange.MonthlyEarnings;
import com.example.qa.exchange.MonthlyEarningsResponse;
import com.example.qa.user.exchange.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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

        //Test for MonthlyEarnings
        LocalDate date = LocalDate.now();
        int earnings = 1;
        MonthlyEarnings earnings1 = new MonthlyEarnings();
        MonthlyEarnings earnings2 = new MonthlyEarnings(date, earnings);
        assertEquals(date, earnings2.getDate());
        assertEquals(earnings, earnings2.getEarnings());
        assertEquals(0, earnings1.getEarnings());
        earnings1.setEarnings(earnings);
        earnings1.setDate(date);
        assertEquals(earnings, earnings1.getEarnings());
        assertEquals(date, earnings1.getDate());


        //Test for MonthlyEarningsResponse
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
         String month = LocalDate.now().format(monthFormatter);
         int monthlyEarnings = 100;
         MonthlyEarnings monthlyEarnings1 = new MonthlyEarnings(LocalDate.now(), monthlyEarnings);
         MonthlyEarningsResponse response1 = new MonthlyEarningsResponse(monthlyEarnings1);
         response1.setEarnings(monthlyEarnings);
         response1.setMonth(month);
         assertEquals(month, response1.getMonth());
         assertEquals(monthlyEarnings, response1.getEarnings());

         //Test for UserStatusResponse
        int askCount = 1;
        int answerCount = 1;
        UserStatsResponse statsResponse = new UserStatsResponse();
        statsResponse.setAnswerCount(answerCount);
        statsResponse.setAskCount(askCount);
        assertEquals(askCount, statsResponse.getAskCount());
        assertEquals(answerCount, statsResponse.getAnswerCount());
    }
}
