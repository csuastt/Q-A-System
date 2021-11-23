package com.example.qa.order.exchange;

import com.example.qa.order.model.Order;
import com.example.qa.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
public class OrderListResponse {
    private int pageSize;
    private int page;
    private int totalPages;
    private long totalCount;
    private OrderResponse[] data;
    private long timeMillis;

    public OrderListResponse(Page<Order> orderPage, int level) {
        this(orderPage, level, null);
    }

    public OrderListResponse(Page<Order> orderPage, int level, User user) {
        pageSize = orderPage.getSize();
        page = orderPage.getNumber() + 1;
        totalPages = orderPage.getTotalPages();
        totalCount = orderPage.getTotalElements();
        data = orderPage.get().map(order -> new OrderResponse(order, level, user)).toArray(OrderResponse[]::new);
    }
}
