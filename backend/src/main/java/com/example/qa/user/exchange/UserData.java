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
	private Long id;
	private String username;
	private String nickname;
	private String avatarUrl;
	@JsonFormat(pattern = "yyyy-MM-dd 'T' HH:mm:ss+hh")
	private ZonedDateTime createTime;
	private String email;
	private String gender;
	private LocalDate birthday;
	private String description;
	private String permission;
	private String phone;
	private Integer money;

	/**
	 * @param appUser Construct from restored user
	 */
	public UserData(AppUser appUser) {
		this.id = appUser.getId();
		this.nickname = appUser.getNickname();
		this.username = appUser.getUsername();
		this.avatarUrl = appUser.getAva_url();
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
