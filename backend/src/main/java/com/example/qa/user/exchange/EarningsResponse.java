package com.example.qa.user.exchange;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EarningsResponse {
    private int total;
    private MonthlyEarningsResponse[] monthly;

    public EarningsResponse(int total, List<MonthlyEarnings> monthlyEarningsList) {
        this.total = total;
        monthly = monthlyEarningsList.stream().map(MonthlyEarningsResponse::new).toArray(MonthlyEarningsResponse[]::new);
    }
}
