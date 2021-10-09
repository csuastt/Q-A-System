package com.example.qa.user.exchange;

import lombok.Data;

import java.util.ArrayList;

/**
 *  Response Body requesting /api/users
 *  METHOD:GET
 */
@Data
public class GetAllData {
    ArrayList<UserData> users = new ArrayList<>();
}
