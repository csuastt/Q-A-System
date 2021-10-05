package com.example.qa.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetAllResponse {

    @JsonProperty("users")
    private List<UsersDTO> users;

    @NoArgsConstructor
    @Data
    public static class UsersDTO {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("username")
        private String username;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("ava_url")
        private String avaUrl;
        @JsonProperty("password")
        private String password;
        @JsonProperty("email")
        private String email;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("birthday")
        private String birthday;
        @JsonProperty("gender")
        private String gender;
        @JsonProperty("enable")
        private Boolean enable;
        @JsonProperty("permit")
        private String permit;
        @JsonProperty("money")
        private Integer money;
        @JsonProperty("description")
        private String description;
        @JsonProperty("sign_up_timestamp")
        private Integer signUpTimestamp;
        @JsonProperty("authorities")
        private List<AuthoritiesDTO> authorities;
        @JsonProperty("enabled")
        private Boolean enabled;
        @JsonProperty("credentialsNonExpired")
        private Boolean credentialsNonExpired;
        @JsonProperty("accountNonExpired")
        private Boolean accountNonExpired;
        @JsonProperty("accountNonLocked")
        private Boolean accountNonLocked;

        @NoArgsConstructor
        @Data
        public static class AuthoritiesDTO {
            @JsonProperty("authority")
            private String authority;
        }
    }
}
