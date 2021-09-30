package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class ModifyPassResponse {
    public String code;
    public String message;

    public ModifyPassResponse(String code, String message){
        this.code = code;
        this.message = message;
    }

}
