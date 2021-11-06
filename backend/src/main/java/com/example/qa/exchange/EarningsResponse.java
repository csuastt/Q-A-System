package com.example.qa.exchange;

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

    public EarningsResponse(int total, List<MonthlyEarnings> monthlyList) {
        this.total = total;
        monthly = monthlyList.stream().map(MonthlyEarningsResponse::new).toArray(MonthlyEarningsResponse[]::new);
    }
}
