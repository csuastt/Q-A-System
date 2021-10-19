package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class ModifyManagerPasswordAttribute {
    private String originPassword;
    private String newPassword;
}
