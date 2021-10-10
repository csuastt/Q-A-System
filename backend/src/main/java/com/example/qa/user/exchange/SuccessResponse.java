package com.example.qa.user.exchange;

import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Response Body getting attributes
 * With Successful information
 */
@Data
@ResponseBody
public class SuccessResponse {
    public String message;

    public SuccessResponse(String message){
        this.message = message;
    }
}
