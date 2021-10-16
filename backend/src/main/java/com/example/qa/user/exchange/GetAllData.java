package com.example.qa.user.exchange;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 *  Response Body requesting /api/users
 *  METHOD:GET
 */
@Data
@NoArgsConstructor
public class GetAllData {
    ArrayList<BasicUserData> users = new ArrayList<>();
}
