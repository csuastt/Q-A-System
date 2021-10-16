package com.example.qa.user.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Response Body getting attributes
 * With Successful information
 */
@Data
@NoArgsConstructor
@ResponseBody
public class SuccessResponse {
    private String message;

    public SuccessResponse(String message){
        this.message = message;
    }
}
