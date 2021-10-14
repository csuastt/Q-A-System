package com.example.qa.manager.exchange;


import com.example.qa.manager.model.AppManager;

public class AuthenticationSuccessResponse {
    public String token;
    public ManagerData manager;

    public AuthenticationSuccessResponse(String token, AppManager appManager) {
        this.token = token;
        this.manager = new ManagerData(appManager);
    }
}
