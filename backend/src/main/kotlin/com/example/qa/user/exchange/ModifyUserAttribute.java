package com.example.qa.user.exchange;

import lombok.Data;

@Data
public class ModifyUserAttribute {
    public String username;
    public String password;
    public String email;
    public String birthday;
    public String gender;
    public String nickname;
    public String phone;
    public String description;
}
