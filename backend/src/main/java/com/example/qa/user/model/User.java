package com.example.qa.user.model;

import com.example.qa.user.exchange.ApplyRequest;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.exchange.UserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User implements UserDetails {
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
    private String description = "";

    private ZonedDateTime createTime;

    private UserRole role = UserRole.USER;

    private int balance = 100;

    public User(RegisterRequest registerRequest) {
        username = registerRequest.getUsername();
        password = registerRequest.getPassword();
        nickname = registerRequest.getUsername();
        email = registerRequest.getEmail();
        createTime = ZonedDateTime.now();
    }

    public void update(UserRequest data, boolean isAdmin) {
        nickname = data.getNickname() != null ? data.getNickname() : nickname;
        phone = data.getPhone() != null ? data.getPhone() : phone;
        gender = data.getGender() != null ? data.getGender() : gender;
        price = data.getPrice() != null ? data.getPrice() : price;
        description = data.getDescription() != null ? data.getDescription() : description;
        if (isAdmin) {
            email = data.getEmail() != null ? data.getEmail() : email;
            role = data.getRole() != null ? data.getRole() : role;
            balance = data.getBalance() != null ? data.getBalance() : balance;
        }
    }

    public void update(ApplyRequest data) {
        description = data.getDescription();
        price = data.getPrice();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !deleted;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !deleted;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !deleted;
    }
}
