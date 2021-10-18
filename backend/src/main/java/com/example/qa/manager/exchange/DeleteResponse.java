package com.example.qa.manager.exchange;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteResponse {
    public String message;

    public DeleteResponse(String message) {
        this.message = message;
    }

}
