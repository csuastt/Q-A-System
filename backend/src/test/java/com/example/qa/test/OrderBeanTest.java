package com.example.qa.test;

import static org.junit.jupiter.api.Assertions.assertEquals;


import com.example.qa.order.exchange.AcceptRequest;
import com.example.qa.order.exchange.AnswerRequest;
import com.example.qa.order.exchange.OrderListResponse;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderBeanTest {

    @Test
    void testSetProperty(){

        // Test for AcceptRequest
        boolean accept = true;
        AcceptRequest request = new AcceptRequest(true);
        request.setAccept(accept);
        assertEquals(accept, request.isAccept());

        // Test for OrderListResponse
        int pageSize = 1;
        int page = 1;
        int totalPages = 1;
        long totalCount = 1;
        OrderResponse[] data = new OrderResponse[]{};
        OrderListResponse response = new OrderListResponse();
        response.setPageSize(pageSize);
        response.setPage(page);
        response.setTotalPages(totalPages);
        response.setTotalCount(totalCount);
        response.setData(data);
        assertEquals(pageSize, response.getPageSize());
        assertEquals(page, response.getPage());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(totalCount, response.getTotalCount());
        assertEquals(data, response.getData());

        //Test for AnswerRequest
        String answer = "This is a test";
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setAnswer(answer);
        assertEquals(answer, answerRequest.getAnswer());
    }
}
