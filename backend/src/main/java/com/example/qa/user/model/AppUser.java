package com.example.qa.user.model;

import com.example.qa.user.exchange.UserAttribute;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;

/**
 * Entity restored in the repository
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "APPUSER")
public class AppUser implements UserDetails {
	@Id @GeneratedValue
	private Long id;
	@Column
	private String username;
	private String nickname = "";
	private String ava_url = "";
	private String password;
	private String email = "";
	private String phone = "";
	private LocalDate birthday = LocalDate.parse("2000-01-01");
	private String gender = "unknown";
	private Boolean enable = true;
	private String permit = "q";
	private Integer money = 100;
    // TODO: It may not be the best way to deal with database updates. We will change it after the stabilization of data models
	@ColumnDefault("0")
	private Integer price = 0;
	private String description = "";
//	@JsonFormat(shape=JsonFormat.Shape.STRING)
//	@JsonFormat(pattern = "yyyy-MM-dd'T'HH：mm：ss.SSSZ")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX][XX]")
	public ZonedDateTime createTime;

	@ElementCollection(fetch = FetchType.EAGER)
	private Collection<GrantedAuthority> authorities;

	/**
	 * Construct User from username and password
	 * @param username    User name
	 * @param password    Initial password
	 * @param authorities Initial authorities
	 */
	public AppUser(String username,
				   String password,
				   Collection<GrantedAuthority> authorities){
		this.ava_url = "api/users/avatar/" + 1 + ".png";
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.createTime = ZonedDateTime.now();
	}

	/**
	 * init ava path
	 */
	public void setAva(){
		this.ava_url = "api/users/avatar/" + id + ".png";
	}

	/**
	 * Construct User from registration
	 * @param register Request Body when register
	 */
	public AppUser(UserAttribute register){
		if (register.username != null)
			this.username = register.username;
		if (register.password != null)
			this.password = register.password;
		if (register.birthday != null)
			this.birthday = LocalDate.parse(register.birthday);
		if (register.gender != null)
			this.gender = register.gender;
		if (register.email != null)
			this.email = register.email;
		if (register.nickname != null)
			this.nickname = register.nickname;
		if(register.phone != null)
			this.phone = register.phone;
		if(register.description != null)
			this.description = register.description;
		this.createTime = ZonedDateTime.now();
	}

	/**
	 * Update info from modification
	 * @param newInfo Request Body when modify
	 */
	public void updateUserInfo(UserAttribute newInfo) {
		if (newInfo.username != null)
			this.username = newInfo.username;
		if (newInfo.birthday != null)
			this.birthday = LocalDate.parse(newInfo.birthday);
		if (newInfo.gender != null)
			this.gender = newInfo.gender;
		if (newInfo.email != null)
			this.email = newInfo.email;
		if (newInfo.nickname != null)
			this.nickname = newInfo.nickname;
		if(newInfo.phone != null)
			this.phone = newInfo.phone;
		if(newInfo.description != null)
			this.description = newInfo.description;
	}

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

	@Override
	public boolean isAccountNonExpired() {
		return enable;
	}

	@Override
	public boolean isAccountNonLocked() {
		return enable;
	}

	@Override
	public boolean isEnabled() {
		return enable;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return enable;
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
