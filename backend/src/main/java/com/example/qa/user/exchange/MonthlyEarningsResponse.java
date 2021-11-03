package com.example.qa.user.exchange;

import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class MonthlyEarningsResponse {
    private String month;
    private int earnings;
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    public MonthlyEarningsResponse(MonthlyEarnings monthlyEarnings) {
        month = monthlyEarnings.getDate().format(monthFormatter);
        earnings = monthlyEarnings.getEarnings();
    }
}
