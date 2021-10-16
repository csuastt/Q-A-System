package com.example.qa.user.exchange;

import lombok.Data;

/**
 *  Request Body visiting /api/users/{id}
 *  METHOD:PUT
 */
@Data
public class UserAttribute {
    private String username;
    private String password;
    private String email;
    private String birthday;
    private String gender;
    private String nickname;
    private String phone;
    private String description;
}
