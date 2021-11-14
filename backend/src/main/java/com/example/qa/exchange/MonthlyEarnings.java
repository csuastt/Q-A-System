package com.example.qa.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.qa.utils.JsonUtils.mapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyEarnings {
    private LocalDate date;
    private int earnings = 0;

    public static List<MonthlyEarnings> toList(String monthlyEarnings) {
        try {
            return mapper.readerForListOf(MonthlyEarnings.class).readValue(monthlyEarnings);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public static String addEarnings(String monthlyStr, int earnings) {
        List<MonthlyEarnings> monthlyList = toList(monthlyStr);
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        if (monthlyList.isEmpty() || !monthlyList.get(monthlyList.size() - 1).getDate().isEqual(thisMonth)) {
            monthlyList.add(new MonthlyEarnings(thisMonth, earnings));
        } else {
            MonthlyEarnings monthly = monthlyList.get(monthlyList.size() - 1);
            monthly.setEarnings(monthly.getEarnings() + earnings);
        }
        try {
            return mapper.writeValueAsString(monthlyList);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
