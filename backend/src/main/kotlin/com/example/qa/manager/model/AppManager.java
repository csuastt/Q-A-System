package com.example.qa.manager.model;


import com.example.qa.manager.exchange.ModifyManagerAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.Id;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class AppManager implements UserDetails {
    @Id
    private String managername;
    private String password;
    private String nickname = "";
    private String permission = "observer";
    private String email = "";
    private String phone = "";
    public Long create_up_timestamp;


    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<GrantedAuthority> authorities;


    public AppManager(String managername,
                   String password,
                   Collection<GrantedAuthority> authorities){
        this.managername = managername;
        this.password = password;
        this.authorities = authorities;
        this.create_up_timestamp = Instant.now().getEpochSecond();
    }

    public AppManager(String managername,
                      String password,
                      Collection<GrantedAuthority> authorities,
                      String permission,
                      String email,
                      String phone,
                      String nickname) {
        this.managername = managername;
        this.password = password;
        this.authorities = authorities;
        this.permission = permission;
        this.create_up_timestamp = Instant.now().getEpochSecond();
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
    }



    public void updateManagerInfo(ModifyManagerAttribute newInfo) {
        if (newInfo.managername != null)
            this.managername = newInfo.managername;
        if (newInfo.permission != null)
            this.permission = newInfo.permission;
        if (newInfo.password != null)
            this.password = newInfo.password;
        if (newInfo.email != null)
            this.email = newInfo.email;
        if (newInfo.nickname != null)
            this.nickname = newInfo.nickname;
        if(newInfo.phone != null)
            this.phone = newInfo.phone;
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
        return Objects.equals(managername, appManager.managername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managername,password,permission,email, phone,nickname);
    }



}
