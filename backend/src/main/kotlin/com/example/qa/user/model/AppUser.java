package com.example.qa.user.model;

import com.example.qa.user.exchange.ModifyUserAttribute;
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
import javax.persistence.Id;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class AppUser implements UserDetails {
	@Id
	private String username;
	private String nickname = "";
	private String ava_url = "";
	private String password;
	private String email = "";
	private String phone = "";
	private String birthday = "";
	private String gend = "Unknown";
	private boolean enable = true;
	private String permit = "q";
	private String money = "100";
	private String description = "";
	public Long sign_up_timestamp;


	@ElementCollection(fetch = FetchType.EAGER)
	private Collection<GrantedAuthority> authorities;

	public AppUser(String username,
				   String password,
				   Collection<GrantedAuthority> authorities){
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.ava_url = "/avatar/" + username + ".png";
		this.sign_up_timestamp = Instant.now().getEpochSecond();
	}

	public AppUser(String username,
				   String password,
				   Collection<GrantedAuthority> authorities,
				   String mail,
				   String gend,
				   String money,
				   String description,
				   String nickname,
				   String permission,
				   Boolean enable,
				   String phone,
				   String birthday) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.ava_url = "/avatar/" + username + ".png";
		this.sign_up_timestamp = Instant.now().getEpochSecond();
		this.email = mail;
		this.gend = gend;
		this.money = money;
		this.description = description;
		this.nickname = nickname;
		this.permit = permission;
		this.phone = phone;
		this.enable = enable;
		this.birthday = birthday;
	}

	public void updateUserInfo(ModifyUserAttribute newInfo) {
		if (newInfo.username != null)
			this.username = newInfo.username;
		if (newInfo.password != null)
			this.password = newInfo.password;
		if (newInfo.birthday != null)
			this.birthday = newInfo.birthday;
		if (newInfo.gender != null)
			this.gend = newInfo.gender;
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
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override 
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
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
		return Objects.hash(username,password, email, phone, birthday, gend, nickname);
	}
}
