package com.example.qa.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SystemStatsResponse {
    private long userCount;
    private long answererCount;
    private long orderCount;
    private long orderToReviewCount;
    private long publicOrderCount;
    private long adminCount;
}
