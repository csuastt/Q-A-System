package com.example.qa.user.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginResponse {

    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private UserDTO user;

    @NoArgsConstructor
    @Data
    public static class UserDTO {
        @SerializedName("id")
        private Integer id;
        @SerializedName("username")
        private String username;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("avatar_url")
        private String avatarUrl;
        @SerializedName("sign_up_timestamp")
        private Integer signUpTimestamp;
        @SerializedName("email")
        private String email;
        @SerializedName("gender")
        private String gender;
        @SerializedName("birthday")
        private String birthday;
    }
}
