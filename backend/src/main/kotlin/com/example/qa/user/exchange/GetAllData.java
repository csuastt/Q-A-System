package com.example.qa.user.exchange;

import com.example.qa.user.model.AppUser;
import lombok.Data;

import java.util.ArrayList;

/**
 *  Response Body requesting /api/users
 *  METHOD:GET
 */
@Data
public class GetAllData {
    ArrayList<AppUser> users = new ArrayList<>();
}
