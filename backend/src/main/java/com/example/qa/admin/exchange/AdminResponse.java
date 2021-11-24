package com.example.qa.admin.exchange;

import com.example.qa.admin.model.Admin;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AdminResponse {
    private Long id;
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime createTime;
    private Admin.Role role;

    public AdminResponse(Admin admin) {
        id = admin.getId();
        username = admin.getUsername();
        createTime = admin.getCreateTime();
        role = admin.getRole();
    }
}
