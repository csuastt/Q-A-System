package com.example.qa.user.model;

import com.example.qa.user.exchange.ApplyRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.UserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean deleted = false;

    @Column(unique = true)
    private String username;  // 删除时手动在之后添加 @{id} 以便允许重新注册
    private String password;

    private String avatar;
    private String nickname;
    private String email;
    private String phone = "";
    private Gender gender = Gender.UNKNOWN;

    private int price = 0;
    @Lob
    private String description = "";

    private ZonedDateTime createTime;

    private UserRole role = UserRole.USER;

    private int balance = 100;

    private int earningsTotal = 0;
    @Lob
    private String earningsMonthly = "[]";

    private int askCount = 0;
    private int answerCount = 0;

    public User(RegisterRequest registerRequest) {
        username = registerRequest.getUsername();
        password = registerRequest.getPassword();
        nickname = registerRequest.getUsername();
        email = registerRequest.getEmail();
        createTime = ZonedDateTime.now();
    }

    public void update(UserRequest data, boolean isAdmin) {
        nickname = Objects.requireNonNullElse(data.getNickname(), nickname);
        phone = Objects.requireNonNullElse(data.getPhone(), phone);
        gender = Objects.requireNonNullElse(data.getGender(), gender);
        price = Objects.requireNonNullElse(data.getPrice(), price);
        description = Objects.requireNonNullElse(data.getDescription(), description);
        if (isAdmin) {
            email = Objects.requireNonNullElse(data.getEmail(), email);
            role = Objects.requireNonNullElse(data.getRole(), role);
            balance = Objects.requireNonNullElse(data.getBalance(), balance);
        }
    }

    public void update(ApplyRequest data) {
        description = data.getDescription();
        price = data.getPrice();
    }
}
