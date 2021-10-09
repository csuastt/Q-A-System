package com.example.qa.user.exchange;

import lombok.Data;

/**
 * Response Body requesting /api/users/{id}/permission
 * METHOD:GET
 */
@Data
public class QuestPermit {
    public String permit;

    public QuestPermit(String permit){
        this.permit = permit;
    }
}
