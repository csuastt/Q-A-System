package com.example.qa.order.exchange;

import com.example.qa.order.model.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    private Long asker;
    private Long answerer;
    private Order.State state;
    private Order.EndReason endReason;
    private String title;
    private String description;
    private String answer;
    private Integer price;
    private Boolean showPublic;
}
