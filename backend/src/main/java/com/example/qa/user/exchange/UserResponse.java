package com.example.qa.user.exchange;

import com.example.qa.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private User.Gender gender;
    private Integer price;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime createTime;
    private User.Role role;
    private Boolean applying;
    private Integer balance;
    private Integer askCount;
    private Integer answerCount;
    private Integer ratingCount;
    private Integer ratingTotal;
    private Double rating;

    public UserResponse(User user) {
        this(user, 0);
    }

    public UserResponse(User user, int level) {
        id = user.getId();
        username = user.getUsername();
        nickname = user.getNickname();
        description = user.getDescription();
        if (user.getRole() == User.Role.ANSWERER) {
            price = user.getPrice();
            ratingCount = user.getRatingCount();
            ratingTotal = user.getRatingTotal();
            rating = user.getRating();
        }
        if (level >= 1) {  // 本用户或管理员
            email = user.getEmail();
            phone = user.getPhone();
            gender = user.getGender();
            role = user.getRole();
            applying = user.isApplying();
            balance = user.getBalance();
            askCount = user.getAskCount();
            answerCount = user.getAnswerCount();
        }
        if (level >= 2) {  // 管理员
            createTime = user.getCreateTime();
        }
    }
}
