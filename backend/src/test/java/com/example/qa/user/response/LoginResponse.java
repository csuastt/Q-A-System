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
        private Long id;
        @SerializedName("username")
        private String username;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("avatar_url")
        private String avatarUrl;
        @SerializedName("createTime")
        private String createTime;
        @SerializedName("email")
        private String email;
        @SerializedName("gender")
        private String gender;
        @SerializedName("birthday")
        private String birthday;
        @SerializedName("description")
        private String description;
        @SerializedName("permission")
        private String permission;
        @SerializedName("phone")
        private String phone;
        @SerializedName("money")
        private Integer money;
    }
}
