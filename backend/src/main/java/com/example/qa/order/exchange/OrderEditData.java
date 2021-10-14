package com.example.qa.order.exchange;

import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderEditData {
    private Long asker;
    private Long answerer;
    private OrderState state;
    private OrderEndReason endReason;
    private String question;
    private Integer price;
}
