package com.example.qa.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@Data
public class GetUserResponse {

    @SerializedName("id")
    private Long id;
    @SerializedName("username")
    private String username;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX][XX]")
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
}
