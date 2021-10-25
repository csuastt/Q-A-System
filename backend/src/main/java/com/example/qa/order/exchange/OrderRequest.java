package com.example.qa.order.exchange;

import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    private Long asker;
    private Long answerer;
    private OrderState state;
    private OrderEndReason endReason;
    private String question;
    private Integer price;
}
