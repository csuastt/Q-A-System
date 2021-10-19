package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class QuestPermission {
    private String code;
    private String permission;

    public QuestPermission(String code, String permission){
        this.code = code;
        this.permission = permission;
    }

}
