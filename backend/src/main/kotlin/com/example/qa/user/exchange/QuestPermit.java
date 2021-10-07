package com.example.qa.user.exchange;

/**
 * Response Body requesting /api/users/{id}/permission
 * METHOD:GET
 */
public class QuestPermit {
    public String permit;

    public QuestPermit(String permit){
        this.permit = permit;
    }
}
