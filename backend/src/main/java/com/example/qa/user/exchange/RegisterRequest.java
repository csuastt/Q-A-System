package com.example.qa.user.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String gender;
    private String phone;
}
