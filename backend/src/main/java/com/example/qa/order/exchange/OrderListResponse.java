package com.example.qa.order.exchange;

import com.example.qa.order.model.Order;
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
    private long totalOrders;
    private OrderResponse[] orders;

    public OrderListResponse(Page<Order> orderPage, int level) {
        pageSize = orderPage.getSize();
        page = orderPage.getNumber() + 1;
        totalPages = orderPage.getTotalPages();
        totalOrders = orderPage.getTotalElements();
        orders = orderPage.get().map(order -> new OrderResponse(order, level)).toArray(OrderResponse[]::new);
    }
}
