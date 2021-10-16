package com.example.qa.user.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetBasicUserResponse {
    @SerializedName("id")
    private Long id;
    @SerializedName("username")
    private String username;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("avatarUrl")
    private String avatarUrl;
    @SerializedName("description")
    private String description;
}
