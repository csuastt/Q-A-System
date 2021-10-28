package com.example.qa.admin.model;

import com.example.qa.admin.exchange.CreateAdminRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean deleted = false;

    @Column(unique = true)
    private String username;  // 删除时手动在之后添加 @{id} 以便允许重新注册
    private String password;

    private ZonedDateTime createTime;

    private AdminRole role = AdminRole.ADMIN;

    public Admin(CreateAdminRequest request) {
        username = request.getUsername();
        password = request.getPassword();
        createTime = ZonedDateTime.now();
        role = request.getRole();
    }
}
