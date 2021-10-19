package com.example.qa.manager.exchange;

import com.example.qa.manager.model.AppManager;
import lombok.Data;

@Data
public class ManagerData {
    private Long id;
    private String managername;
    private String nickname;
    private Long createUpTimestamp;
    private String email;
    private String phone;
    private String permission;

    public ManagerData(AppManager appManager) {
        this.id = appManager.getId();
        this.nickname = appManager.getNickname();
        this.managername = appManager.getManagerName();
        this.createUpTimestamp = appManager.getCreateTime();
        this.email = appManager.getEmail();
        this.phone = appManager.getPhone();
        this.permission = appManager.getPermission();
    }
}
