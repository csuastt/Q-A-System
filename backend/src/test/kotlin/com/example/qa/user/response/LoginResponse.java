package com.example.qa.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginResponse {

    @JsonProperty("token")
    private String token;
    @JsonProperty("user")
    private UserDTO user;

    @NoArgsConstructor
    @Data
    public static class UserDTO {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("username")
        private String username;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("avatar_url")
        private String avatarUrl;
        @JsonProperty("sign_up_timestamp")
        private Integer signUpTimestamp;
        @JsonProperty("mail")
        private String mail;
        @JsonProperty("gender")
        private String gender;
        @JsonProperty("birthday")
        private String birthday;
    }
}