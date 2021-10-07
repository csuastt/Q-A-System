package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;

/**
 *  Response Body when successfully authenticated
 */
public class AuthenticationSuccessResponse {
	public String token;
	public UserData user;

	/**
	 * @param token    Add to request head for next request
	 * @param appUser  User detail information
	 */
	public AuthenticationSuccessResponse(String token, AppUser appUser) {
		this.token = token;
		this.user = new UserData(appUser);
	}
}
