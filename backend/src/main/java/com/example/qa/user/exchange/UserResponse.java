package com.example.qa.user.exchange;

import com.example.qa.user.model.Gender;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
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
    private Boolean deleted;
    private String username;
    private String avatar;
    private String nickname;
    private String email;
    private String phone;
    private Gender gender;
    private Integer price;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime createTime;
    private UserRole role;
    private Integer balance;
    private Integer askCount;
    private Integer answerCount;

    public UserResponse(User user) {
        this(user, 0);
    }

    public UserResponse(User user, int level) {
        id = user.getId();
        username = user.getUsername();
        nickname = user.getNickname();
        avatar = user.getAvatar();
        description = user.getDescription();
        price = user.getRole() == UserRole.ANSWERER ? user.getPrice() : null;
        if (level >= 1) {  // 本用户或管理员
            email = user.getEmail();
            phone = user.getPhone();
            gender = user.getGender();
            role = user.getRole();
            balance = user.getBalance();
            askCount = user.getAskCount();
            answerCount = user.getAnswerCount();
        }
        if (level >= 2) {  // 管理员
            deleted = user.isDeleted();
            createTime = user.getCreateTime();
        }
    }
}
