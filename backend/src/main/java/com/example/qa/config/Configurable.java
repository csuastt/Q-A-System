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
    private Long respondExpirationMilliseconds;
    private Long answerExpirationMilliseconds;
    private Long fulfillExpirationMilliseconds;
    private Integer maxChatMessages;
    private Long maxChatTimeMilliseconds;
}
