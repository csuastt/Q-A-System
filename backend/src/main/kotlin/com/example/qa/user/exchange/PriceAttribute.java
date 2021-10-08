package com.example.qa.user.exchange;

import lombok.Data;

/**
 * Response Body requesting /api/users/{id}/price
 * METHOD:GET
 */
@Data
public class PriceAttribute {
    public int price;

    public PriceAttribute(int price) {
        this.price = price;
    }
}
