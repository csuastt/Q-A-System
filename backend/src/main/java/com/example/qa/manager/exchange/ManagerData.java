package com.example.qa.manager.exchange;

import com.example.qa.manager.model.AppManager;

public class ManagerData {
    public Long id;
    public String managername;
    public String nickname;
    public Long create_up_timestamp;
    public String email;
    public String phone;
    public String permission;

    public ManagerData(AppManager appManager) {
        this.id = appManager.getId();
        this.nickname = appManager.getNickname();
        this.managername = appManager.getManagerName();
        this.create_up_timestamp = appManager.getCreateTime();
        this.email = appManager.getEmail();
        this.phone = appManager.getPhone();
        this.permission = appManager.getPermission();
    }
}
