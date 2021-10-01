package com.example.qa.user.exchange;

import lombok.Data;

@Data
public class ModifyPassResponse {
    public String message;

    public ModifyPassResponse(String message){
        this.message = message;
    }
}
