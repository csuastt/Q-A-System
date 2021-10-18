package com.example.qa.manager.exchange;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyPassResponse {
    public String message;

    public ModifyPassResponse( String message){
        this.message = message;
    }

}
