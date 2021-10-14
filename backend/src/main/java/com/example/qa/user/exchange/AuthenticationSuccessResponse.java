package com.example.qa.user.exchange;


import com.example.qa.user.model.AppUser;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Response Body when successfully authenticated
 */
@Data
@ToString
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
