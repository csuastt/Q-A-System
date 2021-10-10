package com.example.qa.manager.exchange;

import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

@Data
public class DeleteResponse {
    public String message;

    public DeleteResponse(String message) {
        this.message = message;
    }

}
