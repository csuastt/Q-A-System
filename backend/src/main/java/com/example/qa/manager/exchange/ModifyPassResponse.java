package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class ModifyPassResponse {
    private String message;

    public ModifyPassResponse( String message){
        this.message = message;
    }

}
