package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;

public class AuthenticationSuccessResponse {
	public String token;
	public UserData user;

	public AuthenticationSuccessResponse(String token, AppUser appUser) {
		this.token = token;
		this.user = new UserData(appUser);
	}
}
