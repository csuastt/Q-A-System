package com.example.qa.user.exchange;

import lombok.Data;

/**
 *  Request Body visiting /api/users/{id}/password
 *  METHOD:PUT
 */
@Data
public class ModifyPasswordAttribute {
    public String origin;
    public String password;
}
