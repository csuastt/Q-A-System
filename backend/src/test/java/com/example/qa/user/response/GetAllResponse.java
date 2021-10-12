package com.example.qa.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class GetAllResponse {

    @SerializedName("users")
    private List<UsersDTO> users;

    @NoArgsConstructor
    @Data
    public static class UsersDTO {
        @SerializedName("id")
        private Long id;
        @SerializedName("username")
        private String username;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("ava_url")
        private String avaUrl;
        @SerializedName("password")
        private String password;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("birthday")
        private String birthday;
        @SerializedName("gender")
        private String gender;
        @SerializedName("enable")
        private Boolean enable;
        @SerializedName("permission")
        private String permission;
        @SerializedName("money")
        private Integer money;
        @SerializedName("description")
        private String description;
        @SerializedName("createTime")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX][XX]")
        private String createTime;
        @SerializedName("authorities")
        private List<AuthoritiesDTO> authorities;
        @SerializedName("enabled")
        private Boolean enabled;
        @SerializedName("credentialsNonExpired")
        private Boolean credentialsNonExpired;
        @SerializedName("accountNonExpired")
        private Boolean accountNonExpired;
        @SerializedName("accountNonLocked")
        private Boolean accountNonLocked;

        @NoArgsConstructor
        @Data
        public static class AuthoritiesDTO {
            @SerializedName("authority")
            private String authority;
        }
    }
}
