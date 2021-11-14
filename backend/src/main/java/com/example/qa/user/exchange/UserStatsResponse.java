package com.example.qa.user.exchange;

import com.example.qa.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserStatsResponse {
    private int askCount;
    private int answerCount;

    public UserStatsResponse(User user) {
        askCount = user.getAskCount();
        answerCount = user.getAnswerCount();
    }
}
