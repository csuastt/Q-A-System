package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * User detail when requested
 */
@Data
public class UserData {
	public Long id;
	public String username;
	public String nickname;
	public String avatar_url;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+hh")
	public ZonedDateTime createTime;
	public String email;
	public String gender;
	public LocalDate birthday;
	public String description;
	public String permission;
	public String phone;
	public Integer money;

	/**
	 * @param appUser Construct from restored user
	 */
	public UserData(AppUser appUser) {
		this.id = appUser.getId();
		this.nickname = appUser.getNickname();
		this.username = appUser.getUsername();
		this.avatar_url = appUser.getAva_url();
		this.createTime = appUser.createTime;
		this.email = appUser.getEmail();
		this.gender = appUser.getGender();
		this.birthday = appUser.getBirthday();
		this.description = appUser.getDescription();
		this.permission = appUser.getPermit();
		this.phone = appUser.getPhone();
		this.money = appUser.getMoney();
	}
}
