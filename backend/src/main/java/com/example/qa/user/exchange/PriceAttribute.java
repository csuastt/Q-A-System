package com.example.qa.user.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response Body requesting /api/users/{id}/price
 * METHOD:GET
 */
@Data
@NoArgsConstructor
public class PriceAttribute {
    private Integer price;

    public PriceAttribute(int price) {
        this.price = price;
    }
}
