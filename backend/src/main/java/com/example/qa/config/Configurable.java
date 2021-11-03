package com.example.qa.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Configurable {
    private Integer minPrice;
    private Integer maxPrice;
    private Integer respondExpirationSeconds;
    private Integer answerExpirationSeconds;
    private Integer fulfillExpirationSeconds;
    private Integer maxChatMessages;
    private Integer maxChatTimeSeconds;
    private Integer feeRate;
}
