package com.example.qa.user.model;

import com.example.qa.user.exchange.UserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;

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
    private String username;
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

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        createTime = ZonedDateTime.now();
    }

    public void update(UserRequest data) {
        password = data.getPassword() != null ? data.getPassword() : password;
        nickname = data.getNickname() != null ? data.getNickname() : nickname;
        email = data.getEmail() != null ? data.getEmail() : email;
        phone = data.getPhone() != null ? data.getPhone() : phone;
        gender = data.getGender() != null ? data.getGender() : gender;
        price = data.getPrice() != null ? data.getPrice() : price;
        description = data.getDescription() != null ? data.getDescription() : description;
        role = data.getRole() != null ? data.getRole() : role;
        balance = data.getBalance() != null ? data.getBalance() : balance;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return deleted;
    }

    @Override
    public boolean isAccountNonLocked() {
        return deleted;
    }

    @Override
    public boolean isEnabled() {
        return deleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return deleted;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		AppUser appUser = (AppUser) o;
		return Objects.equals(username, appUser.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username,password, email, phone, birthday, gender, nickname);
	}
}
