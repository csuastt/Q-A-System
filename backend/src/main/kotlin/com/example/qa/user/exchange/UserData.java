package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;

public class UserData {
	public Long id;
	public String username;
	public String nickname;
	public String avatar_url;
	public Long sign_up_timestamp;
	public String mail;
	public String gender;
	public String birthday;

	public UserData(AppUser appUser) {
		this.id = appUser.getId();
		this.nickname = appUser.getNickname();
		this.username = appUser.getUsername();
		this.avatar_url = appUser.getAva_url();
		this.sign_up_timestamp = appUser.sign_up_timestamp;
		this.mail = appUser.getEmail();
		this.gender = appUser.getGend();
		this.birthday = appUser.getBirthday();

	}
}
