package com.example.qa.user.exchange;

import com.example.qa.user.model.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicUserData {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String description;

    /**
     * @param userInfo Construct from restored user
     */
    public BasicUserData(AppUser userInfo) {
        this.id = userInfo.getId();
        this.nickname = userInfo.getNickname();
        this.username = userInfo.getUsername();
        this.avatarUrl = userInfo.getAvaUrl();
        this.description = userInfo.getDescription();
    }
}
