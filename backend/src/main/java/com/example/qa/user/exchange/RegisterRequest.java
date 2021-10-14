package com.example.qa.user.exchange;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String gender;
    private String phone;
}
