package com.example.qa.user.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response Body requesting /api/users/{id}/permission
 * METHOD:GET
 */
@Data
@NoArgsConstructor
public class PermitAttribute {
    public String permit;

    public PermitAttribute(String permit){
        this.permit = permit;
    }
}
