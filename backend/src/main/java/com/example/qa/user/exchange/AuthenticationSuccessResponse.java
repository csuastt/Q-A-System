package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *  Response Body when successfully authenticated
 */
@Data
@ToString
@NoArgsConstructor
public class AuthenticationSuccessResponse {
	private String token;
	private UserData user;

	/**
	 * @param token    Add to request head for next request
	 * @param appUser  User detail information
	 */
	public AuthenticationSuccessResponse(String token, AppUser appUser) {
		this.token = token;
		this.user = new UserData(appUser);
	}
}
