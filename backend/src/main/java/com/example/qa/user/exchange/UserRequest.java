package com.example.qa.user.exchange;

import com.example.qa.user.model.Gender;
import com.example.qa.user.model.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String password;
    // private String avatar;
    private String nickname;
    private String email;
    private String phone;
    private Gender gender;
    private Integer price;
    private String description;
    private UserRole role;
    private Integer balance;
}
