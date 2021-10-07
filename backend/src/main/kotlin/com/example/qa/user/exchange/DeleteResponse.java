package com.example.qa.user.exchange;

import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

@Data
@ResponseBody
public class DeleteResponse {
    public String message;

    public DeleteResponse(String message) {
        this.message = message;
    }
}
