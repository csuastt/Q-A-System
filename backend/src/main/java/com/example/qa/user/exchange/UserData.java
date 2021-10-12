package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;

import java.time.ZonedDateTime;

/**
 * User detail when requested
 */
public class UserData {
	public Long id;
	public String username;
	public String nickname;
	public String avatar_url;
	public Long sign_up_timestamp;
	public String email;
	public String gender;
	public String birthday;
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
		this.sign_up_timestamp = appUser.getSign_up_timestamp();
		this.email = appUser.getEmail();
		this.gender = appUser.getGender();
		this.birthday = appUser.getBirthday();
		this.description = appUser.getDescription();
		this.permission = appUser.getPermit();
		this.phone = appUser.getPhone();
		this.money = appUser.getMoney();
	}
}
