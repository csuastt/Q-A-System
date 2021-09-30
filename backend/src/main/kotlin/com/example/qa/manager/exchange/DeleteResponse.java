package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class DeleteResponse {
    public String code;
    public String message;

    public DeleteResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
