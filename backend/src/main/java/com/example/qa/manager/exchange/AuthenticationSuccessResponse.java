package com.example.qa.manager.exchange;


import com.example.qa.manager.model.AppManager;
import lombok.Data;

@Data
public class AuthenticationSuccessResponse {
    private String token;
    private ManagerData manager;

    public AuthenticationSuccessResponse(String token, AppManager appManager) {
        this.token = token;
        this.manager = new ManagerData(appManager);
    }
}
