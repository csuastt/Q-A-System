package com.example.qa.user.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Request Body visiting /api/users/{id}/password
 *  METHOD:PUT
 */
@Data
@NoArgsConstructor
public class ModifyPasswordAttribute {
    private String origin;
    private String password;
}
