package com.example.employeeapp;

import android.util.Log;

public class PtoRequest {
    private final int id;
    private final int requesterId;
    private final String status;
    private final String startDate;
    private final String endDate;
    private final String requestComment;

    public PtoRequest(int id, int requesterId, String status, String startDate,
                      String endDate, String requestComment) {
        this.id = id;
        this.requesterId = requesterId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestComment = requestComment;
    }

    public int getId() {
        return id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public String getStatus() {
        return status;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRequestComment() {
        return requestComment;
    }
}
