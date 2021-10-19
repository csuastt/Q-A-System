package com.example.qa.manager.exchange;

import lombok.Data;

@Data
public class DeleteResponse {
    private String message;

    public DeleteResponse(String message) {
        this.message = message;
    }

}
