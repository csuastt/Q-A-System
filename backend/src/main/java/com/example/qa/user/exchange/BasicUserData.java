package com.example.qa.user.exchange;

import com.example.qa.user.model.AppUser;

public class BasicUserData {
    public Long id;
    public String username;
    public String nickname;
    public String avatar_url;
    public String description;

    /**
     * @param userInfo Construct from restored user
     */
    public BasicUserData(AppUser userInfo) {
        this.id = userInfo.getId();
        this.nickname = userInfo.getNickname();
        this.username = userInfo.getUsername();
        this.avatar_url = userInfo.getAva_url();
        this.description = userInfo.getDescription();
    }
}
