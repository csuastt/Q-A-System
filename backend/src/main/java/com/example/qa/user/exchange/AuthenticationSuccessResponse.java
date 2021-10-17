package com.example.qa.user.exchange;


import com.example.qa.user.model.User;
import lombok.Getter;
import lombok.Setter;

/**
 *  Response Body when successfully authenticated
 */
@Getter
@Setter
public class AuthenticationSuccessResponse {
	private String token;
	private UserResponse user;

	/**
	 * @param token    Add to request head for next request
	 * @param user  User detail information
	 */
	public AuthenticationSuccessResponse(String token, User user) {
		this.token = token;
		this.user = new UserResponse(user, 1);
	}
}
