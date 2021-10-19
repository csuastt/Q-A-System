package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class ModifyManagerAttribute {
    private String managername;
    private String password;
    private String permission;
    private String email;
    private String nickname;
    private String phone;
}
