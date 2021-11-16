package com.example.qa.admin.exchange;

import com.example.qa.admin.model.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {
    private String username;
    private String password;
    private Admin.Role role;
}
