package com.example.qa.user.exchange;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String original;
    private String password;
}
