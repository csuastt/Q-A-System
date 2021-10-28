package com.example.qa.admin.exchange;

import com.example.qa.admin.model.Admin;
import com.example.qa.admin.model.AdminRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AdminResponse {
    private Long id;
    private Boolean deleted;
    private String username;
    private ZonedDateTime createTime;
    private AdminRole role;

    public AdminResponse(Admin admin) {
        id = admin.getId();
        deleted = admin.isDeleted();
        username = admin.getUsername();
        createTime = admin.getCreateTime();
        role = admin.getRole();
    }
}
