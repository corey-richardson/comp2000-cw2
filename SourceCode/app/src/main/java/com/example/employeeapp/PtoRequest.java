package com.example.employeeapp;

import android.util.Log;

public class PtoRequest {
    private int id;
    private int requester_id;
    private String status;
    private String start_date;
    private String end_date;
    private String request_comment;

    public PtoRequest(int id, int requester_id, String status, String start_date,
                      String end_date, String request_comment) {
        this.id = id;
        this.requester_id = requester_id;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.request_comment = request_comment;
    }

    public int getId() {
        return id;
    }

    public int getRequester_id() {
        return requester_id;
    }

    public String getStatus() {
        return status;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getRequest_comment() {
        return request_comment;
    }
}
