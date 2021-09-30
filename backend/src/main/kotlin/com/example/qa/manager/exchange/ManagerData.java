package com.example.qa.manager.exchange;

import com.example.qa.manager.model.AppManager;

public class ManagerData {
    public String managername;
    public String nickname;
    public Long create_up_timestamp;
    public String email;
    public String phone;
    public String permission;

    public ManagerData(AppManager appManager) {
        this.nickname = appManager.getNickname();
        this.managername = appManager.getManagername();
        this.create_up_timestamp = appManager.create_up_timestamp;
        this.email = appManager.getEmail();
        this.phone = appManager.getPhone();
        this.permission = appManager.getPermission();
    }
}
