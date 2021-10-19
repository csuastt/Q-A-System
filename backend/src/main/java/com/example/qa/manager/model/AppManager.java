package com.example.qa.manager.model;


import com.example.qa.manager.exchange.ModifyManagerAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class AppManager implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String managerName;
    private String password;
    private String nickname = "";
    private String permission = "observer";
    private String email = "";
    private String phone = "";
    private Long createTime;


    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<GrantedAuthority> authorities;


    public AppManager(String managerName,
                   String password,
                   Collection<GrantedAuthority> authorities){
        this.managerName = managerName;
        this.password = password;
        this.authorities = authorities;
        this.createTime = Instant.now().getEpochSecond();
    }

    public void updateManagerInfo(ModifyManagerAttribute newInfo) {
        if (newInfo.getManagername() != null)
            this.managerName = newInfo.getManagername();
        if (newInfo.getPermission() != null)
            this.permission = newInfo.getPermission();
        if (newInfo.getPassword() != null)
            this.password = newInfo.getPassword();
        if (newInfo.getEmail() != null)
            this.email = newInfo.getEmail();
        if (newInfo.getNickname() != null)
            this.nickname = newInfo.getNickname();
        if(newInfo.getPhone() != null)
            this.phone = newInfo.getPhone();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return null;
    }

    //账户是否过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //账户是否锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    //用户是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }
    //凭证是否过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    //判断一些其他对象是否等于此
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppManager appManager = (AppManager) o;
        return Objects.equals(managerName, appManager.managerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerName,password,permission,email, phone,nickname);
    }
}
